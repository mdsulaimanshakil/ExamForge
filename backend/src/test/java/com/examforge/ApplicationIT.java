package com.examforge;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.examforge.support.AbstractIntegrationTest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ApplicationIT extends AbstractIntegrationTest {

    @Test
    void healthEndpointIsPublicAndUp() throws Exception {
        mvc.perform(get("/api/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void serverTimeComesFromInjectedClock() throws Exception {
        clock.set(Instant.parse("2026-12-24T10:15:30Z"));

        mvc.perform(get("/api/v1/time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serverTime").value("2026-12-24T10:15:30Z"));
    }

    @Test
    void protectedEndpointWithoutTokenReturnsProblemJson401() throws Exception {
        mvc.perform(get("/api/v1/questions"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    void requestIdIsEchoedOrGenerated() throws Exception {
        mvc.perform(get("/api/v1/time").header("X-Request-Id", "client-req-12345"))
                .andExpect(header().string("X-Request-Id", "client-req-12345"));

        mvc.perform(get("/api/v1/time").header("X-Request-Id", "bad id with spaces"))
                .andExpect(header().string("X-Request-Id", matchesPattern("[0-9a-f]{16}")));
    }

    @Test
    void openApiDocumentIsServed() throws Exception {
        mvc.perform(get("/api/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("ExamForge API"));
    }
}
