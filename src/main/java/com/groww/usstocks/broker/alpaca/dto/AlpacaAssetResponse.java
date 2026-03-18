package com.groww.usstocks.broker.alpaca.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw response DTO from Alpaca Broker API for asset endpoints.
 * <p>
 * This is internal to the Alpaca broker package. It is mapped to the
 * public {@link com.groww.usstocks.dto.response.AssetResponse} via
 * {@link com.groww.usstocks.broker.alpaca.AlpacaResponseMapper}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaAssetResponse {

    private String id;

    @JsonProperty("class")
    private String assetClass;

    private String exchange;

    private String symbol;

    private String name;

    private String status;

    private Boolean tradable;

    private Boolean marginable;

    private Boolean shortable;

    @JsonProperty("easy_to_borrow")
    private Boolean easyToBorrow;

    private Boolean fractionable;

    @JsonProperty("maintenance_margin_requirement")
    private String maintenanceMarginRequirement;
}
