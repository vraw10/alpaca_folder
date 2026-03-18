package com.groww.usstocks.controller;

import java.util.List;

import com.groww.usstocks.dto.request.GetAssetsRequest;
import com.groww.usstocks.dto.response.AssetResponse;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.AssetStatus;
import com.groww.usstocks.model.enums.Exchange;
import com.groww.usstocks.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for asset-related operations.
 * No authentication required — assets are public data.
 */
@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    /**
     * List assets with optional filters.
     * GET /api/v1/assets?status=active&asset_class=us_equity&exchange=NASDAQ
     */
    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAssets(
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(value = "asset_class", required = false) AssetClass assetClass,
            @RequestParam(required = false) Exchange exchange) {
        GetAssetsRequest params = GetAssetsRequest.builder()
                .status(status)
                .assetClass(assetClass)
                .exchange(exchange)
                .build();
        return ResponseEntity.ok(assetService.getAssets(params));
    }

    /**
     * Get a single asset by symbol (e.g. "AAPL") or asset UUID.
     * GET /api/v1/assets/{symbolOrAssetId}
     */
    @GetMapping("/{symbolOrAssetId}")
    public ResponseEntity<AssetResponse> getAssetBySymbolOrId(
            @PathVariable String symbolOrAssetId) {
        return ResponseEntity.ok(assetService.getAssetBySymbolOrId(symbolOrAssetId));
    }
}
