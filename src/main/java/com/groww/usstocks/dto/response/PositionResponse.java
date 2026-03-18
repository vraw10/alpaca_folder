package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for a position held in an account.
 * GET /v1/trading/accounts/{account_id}/positions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionResponse {

    @JsonProperty("asset_id")
    private String assetId;

    private String symbol;

    private Exchange exchange;

    @JsonProperty("asset_class")
    private AssetClass assetClass;

    @JsonProperty("avg_entry_price")
    private String avgEntryPrice;

    private String qty;

    private String side;

    @JsonProperty("market_value")
    private String marketValue;

    @JsonProperty("cost_basis")
    private String costBasis;

    @JsonProperty("unrealized_pl")
    private String unrealizedPl;

    @JsonProperty("unrealized_plpc")
    private String unrealizedPlpc;

    @JsonProperty("current_price")
    private String currentPrice;

    @JsonProperty("lastday_price")
    private String lastdayPrice;

    @JsonProperty("change_today")
    private String changeToday;
}

