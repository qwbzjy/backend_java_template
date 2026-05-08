package com.mall.api.vo.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "物流订单响应")
public class LogisticsOrderVO {

    @Schema(description = "物流单号")
    private String logisticsNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "物流状态：0-待发货，1-已发货，2-运输中，3-待签收，4-已签收，5-拒收，6-退回中，7-已退回")
    private Integer logisticsStatus;

    @Schema(description = "物流公司")
    private String logisticsCompany;

    @Schema(description = "物流公司编码")
    private String logisticsCode;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "签收时间")
    private LocalDateTime signTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}