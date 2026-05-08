package com.mall.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "创建SPU请求")
public class CreateSpuDTO {

    @NotBlank(message = "商品名称不能为空")
    @Schema(description = "商品名称")
    private String name;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "商品描述")
    private String description;

    @NotBlank(message = "主图不能为空")
    @Schema(description = "主图URL")
    private String mainImage;

    @Schema(description = "图片列表JSON")
    private String images;

    @Schema(description = "规格模板JSON")
    private String specTemplate;

    @NotNull(message = "售价不能为空")
    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "成本价")
    private BigDecimal costPrice;
}