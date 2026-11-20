package com.examforge.common.exception;

import org.springframework.http.HttpStatus;

/** 404 — missing, soft-deleted, or not visible to the caller. */
public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, CommonErrorCodes.RESOURCE_NOT_FOUND, message);
    }

    public static NotFoundException of(String resource, Object id) {
        return new NotFoundException(resource + " " + id + " was not found.");
    }
}
