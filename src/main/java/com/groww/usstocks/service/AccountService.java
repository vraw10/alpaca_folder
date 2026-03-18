package com.groww.usstocks.service;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.response.AccountResponse;
import com.groww.usstocks.dto.response.TradingLimitsResponse;
import com.groww.usstocks.validation.AccountIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for account-related operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final BrokerService brokerService;
    private final AccountIdValidator accountIdValidator;

    public AccountResponse getAccount(String accountId) {
        accountIdValidator.validate(accountId);
        return brokerService.getAccount(accountId);
    }

    public TradingLimitsResponse getTradingLimits(String accountId) {
        accountIdValidator.validate(accountId);
        return brokerService.getTradingLimits(accountId);
    }

}
