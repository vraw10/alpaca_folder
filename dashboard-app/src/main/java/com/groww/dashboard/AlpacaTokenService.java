package com.groww.dashboard;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class AlpacaTokenService {

    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 60;

    private final AlpacaConfig config;
    private final RestTemplate restTemplate = new RestTemplate();

    private volatile String cachedAccessToken;
    private volatile Instant tokenExpiresAt = Instant.MIN;

    public AlpacaTokenService(AlpacaConfig config) {
        this.config = config;
    }

    public synchronized String getAccessToken() {
        if (cachedAccessToken != null
                && Instant.now().plusSeconds(TOKEN_REFRESH_BUFFER_SECONDS).isBefore(tokenExpiresAt)) {
            return cachedAccessToken;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
                config.getTokenUrl(),
                new HttpEntity<>(body, headers),
                Map.class);

        cachedAccessToken = (String) response.get("access_token");
        int expiresIn = response.containsKey("expires_in")
                ? ((Number) response.get("expires_in")).intValue()
                : 3600;
        tokenExpiresAt = Instant.now().plusSeconds(expiresIn);

        return cachedAccessToken;
    }
}
