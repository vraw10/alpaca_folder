package com.groww.usstocks.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Vendor-agnostic exception thrown when any broker API returns an error.
 * Wraps the HTTP status for safe handling without exposing internals.
 */
@Getter
public class BrokerApiException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public BrokerApiException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BrokerApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }
}

