package com.groww.usstocks.controller;

import java.util.List;

import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.dto.response.PositionResponse;
import com.groww.usstocks.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for position operations (list and close/liquidate).
 * Account ID is read from the {@code X-Account-Id} header.
 */
@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * Get all open positions.
     * GET /api/v1/positions
     */
    @GetMapping
    public ResponseEntity<List<PositionResponse>> getPositions(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId) {
        return ResponseEntity.ok(positionService.getPositions(accountId));
    }

    /**
     * Get a single open position by symbol (e.g. "AAPL") or asset UUID.
     * GET /api/v1/positions/{symbolOrAssetId}
     */
    @GetMapping("/{symbolOrAssetId}")
    public ResponseEntity<PositionResponse> getPosition(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @PathVariable String symbolOrAssetId) {
        return ResponseEntity.ok(positionService.getPosition(accountId, symbolOrAssetId));
    }

    /**
     * Close/liquidate a specific position by symbol or asset ID.
     * Returns the liquidation order created by the broker.
     * DELETE /api/v1/positions/{symbolOrAssetId}
     */
    @DeleteMapping("/{symbolOrAssetId}")
    public ResponseEntity<OrderResponse> closePosition(
            @RequestHeader(ApiHeaders.ACCOUNT_ID_HEADER) String accountId,
            @PathVariable String symbolOrAssetId) {
        return ResponseEntity.ok(positionService.closePosition(accountId, symbolOrAssetId));
    }
}
