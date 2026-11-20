package com.examforge.common.exception;

/** One invalid field in a {@code VALIDATION_FAILED} problem response. */
public record FieldViolation(String field, String message) {}
