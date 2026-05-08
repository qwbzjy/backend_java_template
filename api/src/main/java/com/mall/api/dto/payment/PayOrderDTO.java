package com.mall.api.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "支付请求")
public class PayOrderDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNo;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式：ALIPAY/WECHAT/BANK_CARD")
    private String paymentMethod;
}