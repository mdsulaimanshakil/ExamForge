package com.examforge.common.exception;

/** Error codes shared by all modules. Module-specific codes live in each module's own class. */
public final class CommonErrorCodes {

    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";
    public static final String INVALID_SORT = "INVALID_SORT";
    public static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String STALE_VERSION = "STALE_VERSION";
    public static final String DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    public static final String INVALID_STATE = "INVALID_STATE";
    public static final String PAYLOAD_TOO_LARGE = "PAYLOAD_TOO_LARGE";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";

    private CommonErrorCodes() {}
}
