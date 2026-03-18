package com.groww.usstocks.marketdata.alpaca.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca latest bars response wrapper.
 * Contains the latest bar keyed by symbol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaLatestBarsResponse {

    private Map<String, AlpacaBar> bars;
}
