package com.groww.usstocks.dto.request;

import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.AssetStatus;
import com.groww.usstocks.model.enums.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query parameters for retrieving a list of assets.
 * GET /v1/assets
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAssetsRequest {

    /**
     * Asset status filter: active, inactive. Default: active.
     */
    private AssetStatus status;

    /**
     * Asset class filter: us_equity, crypto, etc.
     */
    private AssetClass assetClass;

    /**
     * Exchange filter: AMEX, ARCA, BATS, NYSE, NASDAQ, NYSEARCA, OTC.
     */
    private Exchange exchange;
}
