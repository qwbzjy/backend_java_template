package com.mall.api.vo.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "退款记录响应")
public class PaymentRefundVO {

    @Schema(description = "退款流水号")
    private String refundNo;

    @Schema(description = "原支付流水号")
    private String paymentNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款状态：0-待处理，1-退款中，2-退款成功，3-退款失败")
    private Integer refundStatus;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "退款方式")
    private String refundMethod;

    @Schema(description = "退款时间")
    private LocalDateTime refundTime;

    @Schema(description = "第三方退款流水号")
    private String tradeNo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}