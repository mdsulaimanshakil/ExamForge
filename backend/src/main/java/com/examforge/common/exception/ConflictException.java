package com.examforge.common.exception;

import org.springframework.http.HttpStatus;

/** 409 — illegal state transition, duplicates, stale versions, closed attempts, … */
public class ConflictException extends ApiException {

    public ConflictException(String code, String message) {
        super(HttpStatus.CONFLICT, code, message);
    }

    public static ConflictException invalidState(String message) {
        return new ConflictException(CommonErrorCodes.INVALID_STATE, message);
    }

    public static ConflictException duplicate(String message) {
        return new ConflictException(CommonErrorCodes.DUPLICATE_RESOURCE, message);
    }
}
