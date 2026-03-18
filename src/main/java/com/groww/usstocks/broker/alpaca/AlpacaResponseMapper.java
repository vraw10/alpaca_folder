package com.groww.usstocks.broker.alpaca;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.groww.usstocks.broker.alpaca.dto.AlpacaAssetResponse;
import com.groww.usstocks.broker.alpaca.dto.AlpacaCalendarDay;
import com.groww.usstocks.broker.alpaca.dto.AlpacaOrderResponse;
import com.groww.usstocks.dto.response.AssetResponse;
import com.groww.usstocks.dto.response.CalendarDayResponse;
import com.groww.usstocks.dto.response.OrderResponse;
import com.groww.usstocks.model.enums.AssetClass;
import com.groww.usstocks.model.enums.AssetStatus;
import com.groww.usstocks.model.enums.CommissionType;
import com.groww.usstocks.model.enums.Exchange;
import com.groww.usstocks.model.enums.OrderClass;
import com.groww.usstocks.model.enums.OrderSide;
import com.groww.usstocks.model.enums.OrderStatus;
import com.groww.usstocks.model.enums.OrderType;
import com.groww.usstocks.model.enums.PositionIntent;
import com.groww.usstocks.model.enums.TimeInForce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Maps Alpaca-specific DTOs to our internal, vendor-agnostic DTOs.
 * <p>
 * All Alpaca status / enum strings are converted to our own enums here.
 * If Alpaca adds a new status tomorrow, only this class needs updating.
 */
@Slf4j
@Component
public class AlpacaResponseMapper {

    /**
     * Alpaca order status → our internal {@link OrderStatus}.
     * <p>
     * Alpaca statuses:
     *   new, partially_filled, filled, done_for_day, canceled, expired,
     *   replaced, pending_cancel, pending_replace, pending_review,
     *   accepted, pending_new, accepted_for_bidding, stopped,
     *   rejected, suspended, calculated, held
     */
    private static final Map<String, OrderStatus> STATUS_MAP = Map.ofEntries(
            Map.entry("new", OrderStatus.OPEN),
            Map.entry("accepted", OrderStatus.OPEN),
            Map.entry("pending_new", OrderStatus.PENDING),
            Map.entry("accepted_for_bidding", OrderStatus.PENDING),
            Map.entry("pending_review", OrderStatus.PENDING),
            Map.entry("calculated", OrderStatus.PENDING),
            Map.entry("partially_filled", OrderStatus.PARTIALLY_FILLED),
            Map.entry("filled", OrderStatus.FILLED),
            Map.entry("canceled", OrderStatus.CANCELLED),
            Map.entry("expired", OrderStatus.EXPIRED),
            Map.entry("replaced", OrderStatus.REPLACED),
            Map.entry("rejected", OrderStatus.REJECTED),
            Map.entry("stopped", OrderStatus.REJECTED),
            Map.entry("suspended", OrderStatus.REJECTED),
            Map.entry("pending_cancel", OrderStatus.PENDING_CANCEL),
            Map.entry("pending_replace", OrderStatus.PENDING_REPLACE),
            Map.entry("done_for_day", OrderStatus.DONE_FOR_DAY),
            Map.entry("held", OrderStatus.HELD)
    );

    private static final Map<String, OrderSide> SIDE_MAP = Map.of(
            "buy", OrderSide.BUY,
            "sell", OrderSide.SELL
    );

    private static final Map<String, OrderType> TYPE_MAP = Map.of(
            "market", OrderType.MARKET,
            "limit", OrderType.LIMIT,
            "stop", OrderType.STOP,
            "stop_limit", OrderType.STOP_LIMIT,
            "trailing_stop", OrderType.TRAILING_STOP
    );

    private static final Map<String, TimeInForce> TIF_MAP = Map.of(
            "day", TimeInForce.DAY,
            "gtc", TimeInForce.GTC,
            "opg", TimeInForce.OPG,
            "cls", TimeInForce.CLS,
            "ioc", TimeInForce.IOC,
            "fok", TimeInForce.FOK
    );

    private static final Map<String, AssetClass> ASSET_CLASS_MAP = Map.of(
            "us_equity", AssetClass.US_EQUITY,
            "crypto", AssetClass.CRYPTO
    );

    private static final Map<String, Exchange> EXCHANGE_MAP = Map.of(
            "AMEX", Exchange.AMEX,
            "ARCA", Exchange.ARCA,
            "BATS", Exchange.BATS,
            "NYSE", Exchange.NYSE,
            "NASDAQ", Exchange.NASDAQ,
            "NYSEARCA", Exchange.NYSEARCA,
            "OTC", Exchange.OTC
    );

    private static final Map<String, AssetStatus> ASSET_STATUS_MAP = Map.of(
            "active", AssetStatus.ACTIVE,
            "inactive", AssetStatus.INACTIVE
    );

    private static final Map<String, CommissionType> COMMISSION_TYPE_MAP = Map.of(
            "notional", CommissionType.NOTIONAL,
            "qty", CommissionType.QTY,
            "bps", CommissionType.BPS
    );

    private static final Map<String, OrderClass> ORDER_CLASS_MAP = Map.of(
            "simple", OrderClass.SIMPLE,
            "bracket", OrderClass.BRACKET,
            "oco", OrderClass.OCO,
            "oto", OrderClass.OTO,
            "mleg", OrderClass.MLEG
    );

    private static final Map<String, PositionIntent> POSITION_INTENT_MAP = Map.of(
            "buy_to_open", PositionIntent.BUY_TO_OPEN,
            "buy_to_close", PositionIntent.BUY_TO_CLOSE,
            "sell_to_open", PositionIntent.SELL_TO_OPEN,
            "sell_to_close", PositionIntent.SELL_TO_CLOSE
    );

    // ── Public mapping methods ──────────────────────────────────────────

    public OrderResponse toOrderResponse(AlpacaOrderResponse raw) {
        if (raw == null) {
            return null;
        }
        return OrderResponse.builder()
                .id(raw.getId())
                .clientOrderId(raw.getClientOrderId())
                .createdAt(raw.getCreatedAt())
                .updatedAt(raw.getUpdatedAt())
                .submittedAt(raw.getSubmittedAt())
                .filledAt(raw.getFilledAt())
                .expiredAt(raw.getExpiredAt())
                .canceledAt(raw.getCanceledAt())
                .failedAt(raw.getFailedAt())
                .replacedAt(raw.getReplacedAt())
                .replacedBy(raw.getReplacedBy())
                .replaces(raw.getReplaces())
                .assetId(raw.getAssetId())
                .symbol(raw.getSymbol())
                .assetClass(mapAssetClass(raw.getAssetClass()))
                .notional(raw.getNotional())
                .qty(raw.getQty())
                .filledQty(raw.getFilledQty())
                .filledAvgPrice(raw.getFilledAvgPrice())
                .limitPrice(raw.getLimitPrice())
                .stopPrice(raw.getStopPrice())
                .extendedHours(raw.getExtendedHours())
                .trailPrice(raw.getTrailPrice())
                .trailPercent(raw.getTrailPercent())
                .status(mapStatus(raw.getStatus()))
                .side(mapSide(raw.getSide()))
                .type(mapType(raw.getType() != null ? raw.getType() : raw.getOrderType()))
                .timeInForce(mapTimeInForce(raw.getTimeInForce()))
                .commission(raw.getCommission())
                .commissionType(mapCommissionType(raw.getCommissionType()))
                .orderClass(mapOrderClass(raw.getOrderClass()))
                .positionIntent(mapPositionIntent(raw.getPositionIntent()))
                .hwm(raw.getHwm())
                .legs(raw.getLegs() != null
                        ? raw.getLegs().stream().map(this::toOrderResponse).toList()
                        : null)
                .build();
    }

    public List<OrderResponse> toOrderResponseList(List<AlpacaOrderResponse> rawList) {
        if (rawList == null) {
            return Collections.emptyList();
        }
        return rawList.stream().map(this::toOrderResponse).toList();
    }

    // ── Asset mapping ───────────────────────────────────────────────────

    public AssetResponse toAssetResponse(AlpacaAssetResponse raw) {
        if (raw == null) {
            return null;
        }
        return AssetResponse.builder()
                .id(raw.getId())
                .symbol(raw.getSymbol())
                .name(raw.getName())
                .exchange(mapExchange(raw.getExchange()))
                .assetClass(mapAssetClass(raw.getAssetClass()))
                .status(mapAssetStatus(raw.getStatus()))
                .tradable(raw.getTradable())
                .shortable(raw.getShortable())
                .marginable(raw.getMarginable())
                .fractionable(raw.getFractionable())
                .maintenanceMarginRequirement(raw.getMaintenanceMarginRequirement())
                .easyToBorrow(raw.getEasyToBorrow())
                .build();
    }

    public List<AssetResponse> toAssetResponseList(List<AlpacaAssetResponse> rawList) {
        if (rawList == null) {
            return Collections.emptyList();
        }
        return rawList.stream().map(this::toAssetResponse).toList();
    }

    // ── Calendar mapping ────────────────────────────────────────────────

    public CalendarDayResponse toCalendarDayResponse(AlpacaCalendarDay raw) {
        if (raw == null) {
            return null;
        }
        return CalendarDayResponse.builder()
                .date(raw.getDate())
                .open(raw.getOpen())
                .close(raw.getClose())
                .sessionOpen(raw.getSessionOpen())
                .sessionClose(raw.getSessionClose())
                .settlementDate(raw.getSettlementDate())
                .build();
    }

    public List<CalendarDayResponse> toCalendarDayResponseList(List<AlpacaCalendarDay> rawList) {
        if (rawList == null) {
            return Collections.emptyList();
        }
        return rawList.stream().map(this::toCalendarDayResponse).toList();
    }

    // ── Internal mapping helpers ────────────────────────────────────────

    private OrderStatus mapStatus(String alpacaStatus) {
        if (alpacaStatus == null) {
            return OrderStatus.UNKNOWN;
        }
        OrderStatus mapped = STATUS_MAP.get(alpacaStatus.toLowerCase());
        if (mapped == null) {
            log.warn("Unknown Alpaca order status received: '{}', mapping to UNKNOWN", alpacaStatus);
            return OrderStatus.UNKNOWN;
        }
        return mapped;
    }

    private OrderSide mapSide(String alpacaSide) {
        if (alpacaSide == null) {
            return null;
        }
        return SIDE_MAP.get(alpacaSide.toLowerCase());
    }

    private OrderType mapType(String alpacaType) {
        if (alpacaType == null) {
            return null;
        }
        return TYPE_MAP.get(alpacaType.toLowerCase());
    }

    private TimeInForce mapTimeInForce(String alpacaTif) {
        if (alpacaTif == null) {
            return null;
        }
        return TIF_MAP.get(alpacaTif.toLowerCase());
    }

    private AssetClass mapAssetClass(String alpacaAssetClass) {
        if (alpacaAssetClass == null) {
            return null;
        }
        return ASSET_CLASS_MAP.get(alpacaAssetClass.toLowerCase());
    }

    private Exchange mapExchange(String alpacaExchange) {
        if (alpacaExchange == null) {
            return null;
        }
        return EXCHANGE_MAP.get(alpacaExchange);
    }

    private AssetStatus mapAssetStatus(String alpacaStatus) {
        if (alpacaStatus == null) {
            return null;
        }
        return ASSET_STATUS_MAP.get(alpacaStatus.toLowerCase());
    }

    private CommissionType mapCommissionType(String alpacaCommissionType) {
        if (alpacaCommissionType == null) {
            return null;
        }
        return COMMISSION_TYPE_MAP.get(alpacaCommissionType.toLowerCase());
    }

    private OrderClass mapOrderClass(String alpacaOrderClass) {
        if (alpacaOrderClass == null) {
            return null;
        }
        return ORDER_CLASS_MAP.get(alpacaOrderClass.toLowerCase());
    }

    private PositionIntent mapPositionIntent(String alpacaPositionIntent) {
        if (alpacaPositionIntent == null) {
            return null;
        }
        return POSITION_INTENT_MAP.get(alpacaPositionIntent.toLowerCase());
    }
}

