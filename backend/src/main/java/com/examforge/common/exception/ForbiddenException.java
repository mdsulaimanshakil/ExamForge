package com.examforge.common.exception;

import org.springframework.http.HttpStatus;

/** 403 — authenticated but not allowed (wrong role or not the owner of a managed resource). */
public class ForbiddenException extends ApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, CommonErrorCodes.ACCESS_DENIED, message);
    }

    public ForbiddenException(String code, String message) {
        super(HttpStatus.FORBIDDEN, code, message);
    }
}
