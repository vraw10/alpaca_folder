package com.groww.usstocks.controller;

import com.groww.usstocks.dto.response.AccountResponse;
import com.groww.usstocks.dto.response.TradingLimitsResponse;
import com.groww.usstocks.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for account-related operations.
 * Account ID is read from the {@code X-Account-Id} header.
 */
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Get account details.
     * GET /api/v1/account
     */
    @GetMapping
    public ResponseEntity<AccountResponse> getAccount(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    /**
     * Get real-time trading limits (buying power, balance, etc.).
     * GET /api/v1/account/trading-limits
     */
    @GetMapping("/trading-limits")
    public ResponseEntity<TradingLimitsResponse> getTradingLimits(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId) {
        return ResponseEntity.ok(accountService.getTradingLimits(accountId));
    }
}
