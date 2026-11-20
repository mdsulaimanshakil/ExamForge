package com.examforge.common.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Base class for every expected business/API error. Features never edit the global handler; they
 * throw a subclass (or this class) with a stable string {@code code} from their own
 * {@code XxxErrorCodes} constants.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final List<FieldViolation> violations;

    public ApiException(HttpStatus status, String code, String message) {
        this(status, code, message, List.of());
    }

    public ApiException(HttpStatus status, String code, String message, List<FieldViolation> violations) {
        super(message);
        this.status = status;
        this.code = code;
        this.violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public List<FieldViolation> getViolations() {
        return violations;
    }
}
