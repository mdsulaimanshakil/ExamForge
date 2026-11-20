package com.examforge.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Clock;
import java.time.Instant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public server-time endpoint used by the SPA to compute its clock offset. */
@RestController
@RequestMapping("/api/v1/time")
@Tag(name = "System")
public class TimeController {

    private final Clock clock;

    public TimeController(Clock clock) {
        this.clock = clock;
    }

    @GetMapping
    @Operation(
            summary = "Current server time (UTC)",
            security = {})
    public ServerTimeResponse now() {
        return new ServerTimeResponse(clock.instant());
    }

    public record ServerTimeResponse(Instant serverTime) {}
}
