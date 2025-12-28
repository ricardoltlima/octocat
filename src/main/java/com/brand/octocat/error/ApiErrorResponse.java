package com.brand.octocat.error;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String error,
        HttpStatus status,
        String message,
        OffsetDateTime timestamp
) {}


