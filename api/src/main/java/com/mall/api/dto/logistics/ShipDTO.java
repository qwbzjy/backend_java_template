package com.mall.api.dto.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "发货请求")
public class ShipDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号")
    private String orderNo;

    @NotBlank(message = "物流公司不能为空")
    @Schema(description = "物流公司名称")
    private String logisticsCompany;

    @NotBlank(message = "物流公司编码不能为空")
    @Schema(description = "物流公司编码")
    private String logisticsCode;
}