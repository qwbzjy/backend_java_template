package com.mall.logistics.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("logistics_track")
public class LogisticsTrack implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long logisticsId;

    private String logisticsNo;

    private Integer status;

    private String statusDesc;

    private String location;

    private LocalDateTime trackTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}