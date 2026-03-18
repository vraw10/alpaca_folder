package com.groww.usstocks.dto.request;

import com.groww.usstocks.model.enums.OrderQueryStatus;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query parameters for retrieving a list of orders.
 * GET /v1/trading/accounts/{account_id}/orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrdersRequest {

    /**
     * Order status filter: open, closed, all. Default: open.
     */
    private OrderQueryStatus status;

    /**
     * Maximum number of orders to return. Default: 50, Max: 500.
     */
    private Integer limit;

    /**
     * Timestamp to filter orders created after this time (RFC3339).
     */
    private String after;

    /**
     * Timestamp to filter orders created before this time (RFC3339).
     */
    private String until;

    /**
     * Sort direction: asc or desc. Default: desc.
     */
    private SortDirection direction;

    /**
     * If true, includes nested child orders. Default: false.
     */
    private Boolean nested;

    /**
     * Comma-separated list of symbols to filter.
     * If omitted, returns orders for ALL symbols.
     */
    private String symbols;

    /**
     * Filter by order side: buy or sell.
     */
    private OrderSide side;
}
