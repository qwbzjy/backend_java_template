package com.mall.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.payment.entity.PaymentRefund;

import java.math.BigDecimal;

public interface PaymentRefundService extends IService<PaymentRefund> {

    PaymentRefund getByRefundNo(String refundNo);

    String generateRefundNo();

    void createRefund(String paymentNo, String orderNo, Long userId, BigDecimal refundAmount, String refundReason);

    void updateRefundStatus(Long id, Integer status, String tradeNo);
}