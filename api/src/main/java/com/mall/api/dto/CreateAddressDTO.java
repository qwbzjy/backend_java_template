package com.mall.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建地址请求")
public class CreateAddressDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @Schema(description = "收货人手机号")
    private String receiverPhone;

    @NotBlank(message = "省份不能为空")
    @Schema(description = "省份")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Schema(description = "城市")
    private String city;

    @NotBlank(message = "区县不能为空")
    @Schema(description = "区县")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "邮政编码")
    private String postalCode;

    @Schema(description = "地址标签")
    private String tag;

    @Schema(description = "是否默认地址：0-否，1-是")
    private Integer isDefault;
}