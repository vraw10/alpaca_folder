package com.groww.usstocks.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO representing an Alpaca broker account.
 * GET /v1/accounts/{account_id}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountResponse {

    private String id;

    @JsonProperty("account_number")
    private String accountNumber;

    private String status;

    private String currency;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("last_equity")
    private String lastEquity;

    @JsonProperty("account_type")
    private String accountType;
}

