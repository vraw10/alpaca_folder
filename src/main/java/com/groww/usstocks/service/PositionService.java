package com.groww.usstocks.service;

import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.dto.response.PositionResponse;
import com.groww.usstocks.exception.RequestValidationException;
import com.groww.usstocks.validation.AccountIdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for position operations (list and close/liquidate).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionService {

    private final BrokerService brokerService;
    private final AccountIdValidator accountIdValidator;

    public List<PositionResponse> getPositions(String accountId) {
        accountIdValidator.validate(accountId);
        List<PositionResponse> positions = brokerService.getPositions(accountId);
        return positions != null ? positions : Collections.emptyList();
    }

    public PositionResponse getPosition(String accountId, String symbolOrAssetId) {
        accountIdValidator.validate(accountId);
        if (symbolOrAssetId == null || symbolOrAssetId.isBlank()) {
            throw new RequestValidationException("symbol or asset ID must not be blank.");
        }
        return brokerService.getPositionBySymbolOrId(accountId, symbolOrAssetId);
    }

    public OrderResponse closePosition(String accountId, String symbolOrAssetId) {
        accountIdValidator.validate(accountId);
        if (symbolOrAssetId == null || symbolOrAssetId.isBlank()) {
            throw new RequestValidationException("symbol or asset ID must not be blank.");
        }
        log.info("Closing position: account={}, symbol/assetId={}", accountId, symbolOrAssetId);
        return brokerService.closePosition(accountId, symbolOrAssetId);
    }
}
