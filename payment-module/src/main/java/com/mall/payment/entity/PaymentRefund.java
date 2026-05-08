package com.mall.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_refund")
public class PaymentRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String refundNo;

    private String paymentNo;

    private String orderNo;

    private Long userId;

    private BigDecimal refundAmount;

    private Integer refundStatus;

    private String refundReason;

    private String refundMethod;

    private LocalDateTime refundTime;

    private String tradeNo;

    private String callbackContent;

    private LocalDateTime callbackTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}