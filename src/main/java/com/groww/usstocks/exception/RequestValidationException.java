package com.groww.usstocks.exception;

/**
 * Exception thrown when any request validation fails (account ID, order fields, symbols, etc.).
 */
public class RequestValidationException extends RuntimeException {

    public RequestValidationException(String message) {
        super(message);
    }
}
