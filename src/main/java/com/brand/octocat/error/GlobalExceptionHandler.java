package com.brand.octocat.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getUsername());

        ApiErrorResponse body = new ApiErrorResponse(
                "User Not Found",
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClient(RestClientException ex, HttpServletRequest request) {
        log.error("GitHub API error while calling {}", request.getRequestURI(), ex);

        ApiErrorResponse body = new ApiErrorResponse(
                "Upstream Service Error",
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpClientError(HttpClientErrorException ex, HttpServletRequest request) {
        log.error("GitHub returned {} for {} {}", ex.getStatusCode().value(), request.getMethod(), request.getRequestURI(), ex);

        ApiErrorResponse body = new ApiErrorResponse(
                "Upstream Service Error",
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Illegal Argument Exception for request {}", request, ex);

        ApiErrorResponse body = new ApiErrorResponse(
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        ApiErrorResponse body = new ApiErrorResponse(
                "Not Found. Verify URL",
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected application error", ex);

        ApiErrorResponse body = new ApiErrorResponse(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
