package com.mall.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建SKU请求")
public class CreateSkuDTO {

    @NotNull(message = "SPU ID不能为空")
    @Schema(description = "SPU ID")
    private Long spuId;

    @NotBlank(message = "SKU编码不能为空")
    @Schema(description = "SKU编码")
    private String skuCode;

    @NotBlank(message = "规格不能为空")
    @Schema(description = "规格JSON")
    private String specs;

    @NotNull(message = "售价不能为空")
    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "成本价")
    private BigDecimal costPrice;

    @NotNull(message = "库存不能为空")
    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "SKU图片")
    private String image;
}