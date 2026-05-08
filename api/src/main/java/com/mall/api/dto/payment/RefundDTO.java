package com.mall.api.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "退款请求")
public class RefundDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNo;

    @NotNull(message = "退款金额不能为空")
    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @NotBlank(message = "退款原因不能为空")
    @Schema(description = "退款原因")
    private String refundReason;
}