package com.groww.usstocks.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler — returns safe, generic error messages.
 * Internal details are logged but never exposed to the client.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRequestValidation(RequestValidationException ex) {
        log.warn("Request validation failed: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Request validation failed: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        log.warn("Missing required header: {}", ex.getHeaderName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Required header '" + ex.getHeaderName() + "' is missing.");
    }

    @ExceptionHandler(BrokerApiException.class)
    public ResponseEntity<Map<String, Object>> handleBrokerApiError(BrokerApiException ex) {
        log.error("Broker API error: {}", ex.getMessage());
        HttpStatus status = ex.getStatusCode() != null
                ? HttpStatus.valueOf(ex.getStatusCode().value())
                : HttpStatus.BAD_GATEWAY;
        return buildErrorResponse(status, "An error occurred while processing your request with the broker.");
    }

    @ExceptionHandler(MarketDataApiException.class)
    public ResponseEntity<Map<String, Object>> handleMarketDataApiError(MarketDataApiException ex) {
        log.error("Market Data API error: {}", ex.getMessage());
        HttpStatus status = ex.getStatusCode() != null
                ? HttpStatus.valueOf(ex.getStatusCode().value())
                : HttpStatus.BAD_GATEWAY;
        return buildErrorResponse(status, "An error occurred while fetching market data.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
