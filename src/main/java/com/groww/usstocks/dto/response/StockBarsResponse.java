package com.groww.usstocks.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vendor-agnostic historical bars response.
 * Contains bars keyed by symbol, plus pagination token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockBarsResponse {

    private Map<String, List<BarData>> bars;

    @JsonProperty("next_page_token")
    private String nextPageToken;
}
