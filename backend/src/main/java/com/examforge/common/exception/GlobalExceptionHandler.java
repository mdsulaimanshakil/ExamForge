package com.examforge.common.exception;

import com.examforge.common.web.RequestIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Converts every exception into an RFC 9457 {@code application/problem+json} response with the
 * extra properties {@code code}, {@code errors}, {@code traceId} and {@code timestamp}. Feature
 * modules never edit this class — they throw {@link ApiException} subclasses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TYPE_BASE = "https://examforge.app/problems/";

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ProblemDetail> handleApi(ApiException ex, HttpServletRequest request) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("API error {}: {}", ex.getCode(), ex.getMessage(), ex);
        }
        return problem(ex.getStatus(), ex.getCode(), ex.getMessage(), ex.getViolations(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleBodyValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toViolation)
                .toList();
        List<FieldViolation> globals = ex.getBindingResult().getGlobalErrors().stream()
                .map(e -> new FieldViolation(e.getObjectName(), e.getDefaultMessage()))
                .toList();
        return validationProblem(concat(violations, globals), request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ProblemDetail> handleMethodValidation(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldViolation(
                                result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
                .toList();
        return validationProblem(violations, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(v -> new FieldViolation(lastNode(v.getPropertyPath().toString()), v.getMessage()))
                .toList();
        return validationProblem(violations, request);
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        MissingServletRequestParameterException.class,
        MissingServletRequestPartException.class,
        MissingRequestHeaderException.class
    })
    ResponseEntity<ProblemDetail> handleMalformed(Exception ex, HttpServletRequest request) {
        String detail = ex instanceof HttpMessageNotReadableException
                ? "The request body is missing or is not valid JSON for this endpoint."
                : ex.getMessage();
        return problem(HttpStatus.BAD_REQUEST, CommonErrorCodes.MALFORMED_REQUEST, detail, List.of(), request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ProblemDetail> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                CommonErrorCodes.MALFORMED_REQUEST,
                ex.getMessage(),
                List.of(),
                request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ProblemDetail> handleUploadSize(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.CONTENT_TOO_LARGE,
                CommonErrorCodes.PAYLOAD_TOO_LARGE,
                "The uploaded file is too large.",
                List.of(),
                request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    ResponseEntity<ProblemDetail> handleStaleVersion(OptimisticLockingFailureException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                CommonErrorCodes.STALE_VERSION,
                "This resource was modified by someone else. Reload and try again.",
                List.of(),
                request);
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.UNAUTHORIZED,
                CommonErrorCodes.UNAUTHENTICATED,
                "Authentication is required to access this resource.",
                List.of(),
                request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.FORBIDDEN,
                CommonErrorCodes.ACCESS_DENIED,
                "You do not have permission to perform this action.",
                List.of(),
                request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ProblemDetail> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                CommonErrorCodes.RESOURCE_NOT_FOUND,
                "No endpoint " + request.getMethod() + " " + request.getRequestURI() + ".",
                List.of(),
                request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ProblemDetail> handleMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return problem(
                HttpStatus.METHOD_NOT_ALLOWED,
                CommonErrorCodes.METHOD_NOT_ALLOWED,
                ex.getMessage(),
                List.of(),
                request);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                CommonErrorCodes.INTERNAL_ERROR,
                "An unexpected error occurred. Please report the traceId if it persists.",
                List.of(),
                request);
    }

    private ResponseEntity<ProblemDetail> validationProblem(
            List<FieldViolation> violations, HttpServletRequest request) {
        return problem(
                HttpStatus.BAD_REQUEST,
                CommonErrorCodes.VALIDATION_FAILED,
                "One or more fields are invalid.",
                violations,
                request);
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String code,
            String detail,
            List<FieldViolation> violations,
            HttpServletRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, detail);
        body.setType(URI.create(TYPE_BASE + code.toLowerCase(Locale.ROOT).replace('_', '-')));
        body.setTitle(status.getReasonPhrase());
        body.setInstance(URI.create(request.getRequestURI()));
        body.setProperty("code", code);
        if (!violations.isEmpty()) {
            body.setProperty("errors", violations);
        }
        body.setProperty("traceId", RequestIdFilter.currentOrNew());
        body.setProperty("timestamp", clock.instant());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    private static FieldViolation toViolation(FieldError error) {
        return new FieldViolation(error.getField(), error.getDefaultMessage());
    }

    private static String lastNode(String path) {
        int dot = path.lastIndexOf('.');
        return dot >= 0 ? path.substring(dot + 1) : path;
    }

    private static <T> List<T> concat(List<T> a, List<T> b) {
        if (b.isEmpty()) {
            return a;
        }
        return java.util.stream.Stream.concat(a.stream(), b.stream()).toList();
    }
}
