package com.mall.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.payment.entity.PaymentOrder;

import java.util.List;

public interface PaymentOrderService extends IService<PaymentOrder> {

    PaymentOrder getByPaymentNo(String paymentNo);

    PaymentOrder getByOrderNo(String orderNo);

    List<PaymentOrder> getByUserId(Long userId);

    String generatePaymentNo();

    void updatePaymentStatus(Long id, Integer status, String tradeNo);

    boolean isPaid(String orderNo);
}