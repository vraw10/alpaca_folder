package com.groww.usstocks.broker.alpaca;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Alpaca Broker API connection.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "alpaca.broker")
public class AlpacaApiConfig {

    @NotBlank(message = "Alpaca broker base URL must be configured")
    private String baseUrl;

    @NotBlank(message = "Alpaca OAuth2 token URL must be configured")
    private String tokenUrl;

    @NotBlank(message = "Alpaca broker client ID must be configured")
    private String clientId;

    @NotBlank(message = "Alpaca broker client secret must be configured")
    private String clientSecret;

    private int connectTimeoutMs = 5000;

    private int readTimeoutMs = 10000;
}

