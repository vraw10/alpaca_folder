package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vendor-agnostic bar (OHLCV) data point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarData {

    private String timestamp;

    private Double open;

    private Double high;

    private Double low;

    private Double close;

    private Long volume;

    @JsonProperty("trade_count")
    private Long tradeCount;

    private Double vwap;
}
