package com.examforge.common.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

/** 400 — invalid input detected by domain logic (not by Bean Validation). */
public class BadRequestException extends ApiException {

    public BadRequestException(String code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }

    /** Field-level domain validation failure, rendered like Bean Validation errors. */
    public static BadRequestException validation(String field, String message) {
        return validation(List.of(new FieldViolation(field, message)));
    }

    public static BadRequestException validation(List<FieldViolation> violations) {
        return new BadRequestException(violations);
    }

    private BadRequestException(List<FieldViolation> violations) {
        super(
                HttpStatus.BAD_REQUEST,
                CommonErrorCodes.VALIDATION_FAILED,
                "One or more fields are invalid.",
                violations);
    }
}
