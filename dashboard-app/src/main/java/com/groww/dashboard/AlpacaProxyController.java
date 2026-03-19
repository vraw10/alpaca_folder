package com.groww.dashboard;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class AlpacaProxyController {

    private final AlpacaConfig config;
    private final AlpacaTokenService tokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AlpacaProxyController(AlpacaConfig config, AlpacaTokenService tokenService) {
        this.config = config;
        this.tokenService = tokenService;
    }

    @GetMapping("/account")
    public ResponseEntity<String> getAccount(@RequestParam String accountId) {
        String url = config.getBaseUrl() + "/v1/trading/accounts/" + accountId + "/account";
        return proxy(url);
    }

    @GetMapping("/orders")
    public ResponseEntity<String> getOpenOrders(@RequestParam String accountId) {
        String url = config.getBaseUrl() + "/v1/trading/accounts/" + accountId + "/orders?status=open";
        return proxy(url);
    }

    private ResponseEntity<String> proxy(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(tokenService.getAccessToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }
}
