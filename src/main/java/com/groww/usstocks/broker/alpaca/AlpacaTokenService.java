package com.groww.usstocks.broker.alpaca;

import java.time.Instant;
import java.util.Map;

import com.groww.usstocks.exception.BrokerApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Manages OAuth2 access tokens for the Alpaca Broker API.
 * Caches the token and refreshes it automatically before expiry.
 */
@Slf4j
@Component
public class AlpacaTokenService {

    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 60;

    private final AlpacaApiConfig config;
    private final RestTemplate tokenRestTemplate;

    private volatile String cachedAccessToken;
    private volatile Instant tokenExpiresAt = Instant.EPOCH;

    public AlpacaTokenService(AlpacaApiConfig config) {
        this.config = config;
        this.tokenRestTemplate = new RestTemplate();
    }

    /**
     * Returns a valid access token, fetching/refreshing as needed.
     */
    public synchronized String getAccessToken() {
        if (cachedAccessToken == null || Instant.now().isAfter(tokenExpiresAt)) {
            refreshToken();
        }
        return cachedAccessToken;
    }

    private void refreshToken() {
        log.debug("Fetching new OAuth2 access token from Alpaca");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = tokenRestTemplate.postForEntity(
                    config.getTokenUrl(),
                    request,
                    (Class<Map<String, Object>>) (Class<?>) Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("access_token")) {
                throw new BrokerApiException("OAuth2 token response missing access_token", (Throwable) null);
            }

            this.cachedAccessToken = (String) responseBody.get("access_token");

            int expiresIn = 3600;
            if (responseBody.containsKey("expires_in")) {
                Object expiresInObj = responseBody.get("expires_in");
                if (expiresInObj instanceof Number) {
                    expiresIn = ((Number) expiresInObj).intValue();
                }
            }
            this.tokenExpiresAt = Instant.now().plusSeconds(expiresIn - TOKEN_REFRESH_BUFFER_SECONDS);
            log.debug("OAuth2 token obtained, expires in {}s", expiresIn);

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.error("Failed to obtain OAuth2 token: status={}", ex.getStatusCode());
            throw new BrokerApiException("Failed to obtain OAuth2 access token", ex.getStatusCode());
        } catch (RestClientException ex) {
            log.error("Failed to obtain OAuth2 token: connection error");
            throw new BrokerApiException("Failed to obtain OAuth2 access token", ex);
        }
    }
}

