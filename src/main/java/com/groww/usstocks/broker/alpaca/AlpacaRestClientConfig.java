package com.groww.usstocks.broker.alpaca;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the RestTemplate for Alpaca Broker API calls with
 * OAuth2 Bearer token authentication.
 */
@Configuration
public class AlpacaRestClientConfig {

    @Bean
    public RestTemplate alpacaRestTemplate(AlpacaApiConfig config, AlpacaTokenService tokenService) {

        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAccessToken());
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            headers.set(HttpHeaders.ACCEPT, "application/json");
            return execution.execute(request, body);
        };

        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(config.getConnectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(config.getReadTimeoutMs()))
                .interceptors(authInterceptor)
                .build();
    }
}

