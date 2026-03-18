package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for real-time trading limits of an account.
 * GET /v1/trading/accounts/{account_id}/account/trading-limits
 * Used to retrieve current occupied balance and buying power.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradingLimitsResponse {

    @JsonProperty("buying_power")
    private String buyingPower;

    @JsonProperty("regt_buying_power")
    private String regtBuyingPower;

    @JsonProperty("daytrading_buying_power")
    private String daytradingBuyingPower;

    @JsonProperty("cash")
    private String cash;

    @JsonProperty("portfolio_value")
    private String portfolioValue;

    @JsonProperty("equity")
    private String equity;

    @JsonProperty("last_equity")
    private String lastEquity;

    @JsonProperty("long_market_value")
    private String longMarketValue;

    @JsonProperty("short_market_value")
    private String shortMarketValue;

    @JsonProperty("initial_margin")
    private String initialMargin;

    @JsonProperty("maintenance_margin")
    private String maintenanceMargin;

    @JsonProperty("last_maintenance_margin")
    private String lastMaintenanceMargin;

    @JsonProperty("sma")
    private String sma;

    @JsonProperty("non_marginable_buying_power")
    private String nonMarginableBuyingPower;
}

