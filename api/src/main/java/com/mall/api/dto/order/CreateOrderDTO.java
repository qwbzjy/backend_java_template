package com.mall.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建订单请求")
public class CreateOrderDTO {

    @NotNull(message = "收货地址ID不能为空")
    @Schema(description = "收货地址ID")
    private Long addressId;

    @NotEmpty(message = "订单项不能为空")
    @Schema(description = "订单项列表")
    private List<OrderItemDTO> items;

    @Schema(description = "买家备注")
    private String buyerRemark;

    @Data
    public static class OrderItemDTO {
        @NotNull(message = "SKU ID不能为空")
        @Schema(description = "SKU ID")
        private Long skuId;

        @NotNull(message = "购买数量不能为空")
        @Schema(description = "购买数量")
        private Integer quantity;
    }
}