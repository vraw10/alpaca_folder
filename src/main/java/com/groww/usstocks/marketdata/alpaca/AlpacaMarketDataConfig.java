package com.groww.usstocks.marketdata.alpaca;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Alpaca Market Data API connection.
 * Uses API key authentication (not OAuth2).
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "alpaca.market-data")
public class AlpacaMarketDataConfig {

    @NotBlank(message = "Alpaca market data base URL must be configured")
    private String baseUrl;

    @NotBlank(message = "Alpaca API key ID must be configured")
    private String apiKeyId;

    @NotBlank(message = "Alpaca API secret key must be configured")
    private String apiSecretKey;

    private int connectTimeoutMs = 5000;

    private int readTimeoutMs = 10000;
}
