package com.mall.api.vo.logistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "物流轨迹响应")
public class LogisticsTrackVO {

    @Schema(description = "物流单号")
    private String logisticsNo;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "状态描述")
    private String statusDesc;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "轨迹时间")
    private LocalDateTime trackTime;
}