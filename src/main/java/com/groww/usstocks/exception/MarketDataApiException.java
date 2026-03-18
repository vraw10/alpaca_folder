package com.groww.usstocks.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Exception thrown when a market data API returns an error.
 * Same pattern as {@link BrokerApiException} — wraps the HTTP status for safe handling.
 */
@Getter
public class MarketDataApiException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public MarketDataApiException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public MarketDataApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = null;
    }
}
