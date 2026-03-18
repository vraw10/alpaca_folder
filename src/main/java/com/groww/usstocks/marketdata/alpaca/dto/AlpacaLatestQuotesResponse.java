package com.groww.usstocks.marketdata.alpaca.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Raw Alpaca latest quotes response wrapper.
 * Contains the latest quote keyed by symbol.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlpacaLatestQuotesResponse {

    private Map<String, AlpacaQuote> quotes;
}
