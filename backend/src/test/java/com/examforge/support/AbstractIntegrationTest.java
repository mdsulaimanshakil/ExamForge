package com.examforge.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for {@code *IT} tests: full application context, real PostgreSQL via Testcontainers,
 * MockMvc and a controllable clock reset before every test. Module-specific fixtures belong in the
 * module's own test package, not here.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({TestcontainersConfig.class, TestClockConfig.class})
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected MutableClock clock;

    @BeforeEach
    void resetClock() {
        clock.reset();
    }
}
