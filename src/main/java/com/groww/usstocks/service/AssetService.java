package com.groww.usstocks.service;

import java.util.Collections;
import java.util.List;

import com.groww.usstocks.broker.BrokerService;
import com.groww.usstocks.dto.request.GetAssetsRequest;
import com.groww.usstocks.dto.response.AssetResponse;
import com.groww.usstocks.exception.RequestValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service layer for asset-related operations.
 * Assets are public data — no account ID required.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private final BrokerService brokerService;

    public List<AssetResponse> getAssets(GetAssetsRequest params) {
        List<AssetResponse> assets = brokerService.getAssets(params);
        return assets != null ? assets : Collections.emptyList();
    }

    public AssetResponse getAssetBySymbolOrId(String symbolOrAssetId) {
        if (symbolOrAssetId == null || symbolOrAssetId.isBlank()) {
            throw new RequestValidationException("symbol or asset ID must not be blank.");
        }
        return brokerService.getAssetBySymbolOrId(symbolOrAssetId);
    }
}
