package com.groww.usstocks.marketdata.alpaca.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca historical bars response wrapper.
 * Contains bars keyed by symbol, plus pagination token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaBarsResponse {

    private Map<String, List<AlpacaBar>> bars;

    @JsonProperty("next_page_token")
    private String nextPageToken;
}
