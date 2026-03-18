package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.AssetStatus;
import com.groww.usstocks.model.enums.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vendor-agnostic asset response — decoupled from any third-party broker.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetResponse {

    private String id;

    private String symbol;

    private String name;

    private Exchange exchange;

    @JsonProperty("asset_class")
    private AssetClass assetClass;

    private AssetStatus status;

    private Boolean tradable;

    private Boolean shortable;

    private Boolean marginable;

    private Boolean fractionable;

    @JsonProperty("maintenance_margin_requirement")
    private String maintenanceMarginRequirement;

    @JsonProperty("easy_to_borrow")
    private Boolean easyToBorrow;
}
