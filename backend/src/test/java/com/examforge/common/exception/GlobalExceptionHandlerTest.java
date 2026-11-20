package com.examforge.common.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examforge.security.SecurityConfig;
import com.examforge.support.TestClockConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.ThrowingController.class)
@Import({GlobalExceptionHandlerTest.ThrowingController.class, SecurityConfig.class, TestClockConfig.class})
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser
    void apiExceptionKeepsStatusAndCode() throws Exception {
        mvc.perform(get("/test/not-found/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Question 42 was not found."))
                .andExpect(jsonPath("$.type").value("https://examforge.app/problems/resource-not-found"))
                .andExpect(jsonPath("$.instance").value("/test/not-found/42"))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").value("2026-12-01T08:00:00Z"));
    }

    @Test
    @WithMockUser
    void conflictUsesModuleSpecificCode() throws Exception {
        mvc.perform(get("/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ATTEMPT_CLOSED"));
    }

    @Test
    @WithMockUser
    void beanValidationErrorsListEachField() throws Exception {
        mvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"code\":\"TOO-LONG-CODE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'code')]").exists());
    }

    @Test
    @WithMockUser
    void domainValidationUsesSameShape() throws Exception {
        mvc.perform(get("/test/domain-validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.errors[0].field").value("options"))
                .andExpect(jsonPath("$.errors[0].message").value("MCQ questions need exactly one correct option"));
    }

    @Test
    @WithMockUser
    void methodParameterValidation() throws Exception {
        mvc.perform(get("/test/param").param("size", "500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @WithMockUser
    void malformedJsonIsBadRequest() throws Exception {
        mvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @WithMockUser
    void typeMismatchIsBadRequest() throws Exception {
        mvc.perform(get("/test/not-found/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
    }

    @Test
    @WithMockUser
    void optimisticLockIsStaleVersion() throws Exception {
        mvc.perform(get("/test/stale"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("STALE_VERSION"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void methodSecurityDenialIs403Problem() throws Exception {
        mvc.perform(get("/test/admin-only"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    void unauthenticatedIs401Problem() throws Exception {
        mvc.perform(get("/test/conflict"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @WithMockUser
    void unexpectedErrorHidesInternals() throws Exception {
        mvc.perform(get("/test/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.detail")
                        .value("An unexpected error occurred. Please report the traceId if it persists."));
    }

    @Test
    @WithMockUser
    void unknownEndpointIs404Problem() throws Exception {
        mvc.perform(get("/test/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @RestController
    @RequestMapping("/test")
    static class ThrowingController {

        record Payload(@NotBlank String name, @Size(max = 5) String code) {}

        @GetMapping("/not-found/{id}")
        String notFound(@PathVariable long id) {
            throw NotFoundException.of("Question", id);
        }

        @GetMapping("/conflict")
        String conflict() {
            throw new ConflictException("ATTEMPT_CLOSED", "The attempt is closed.");
        }

        @PostMapping("/validate")
        String validate(@Valid @RequestBody Payload payload) {
            return "ok";
        }

        @GetMapping("/domain-validation")
        String domainValidation() {
            throw BadRequestException.validation("options", "MCQ questions need exactly one correct option");
        }

        @GetMapping("/param")
        String param(@RequestParam @Max(100) int size) {
            return "ok";
        }

        @GetMapping("/stale")
        String stale() {
            throw new OptimisticLockingFailureException("stale");
        }

        @GetMapping("/admin-only")
        @PreAuthorize("hasRole('ADMIN')")
        String adminOnly() {
            return "ok";
        }

        @GetMapping("/boom")
        String boom() {
            throw new IllegalStateException("database password is hunter2");
        }
    }
}
