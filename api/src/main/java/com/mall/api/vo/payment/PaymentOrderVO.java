package com.mall.api.vo.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "支付订单响应")
public class PaymentOrderVO {

    @Schema(description = "支付流水号")
    private String paymentNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付状态：0-待支付，1-支付中，2-支付成功，3-支付失败，4-已退款")
    private Integer paymentStatus;

    @Schema(description = "支付方式")
    private String paymentMethod;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "第三方支付流水号")
    private String tradeNo;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}