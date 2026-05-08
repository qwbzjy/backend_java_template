package com.mall.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.payment.entity.PaymentOrder;
import com.mall.payment.mapper.PaymentOrderMapper;
import com.mall.payment.service.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentOrderServiceImpl extends ServiceImpl<PaymentOrderMapper, PaymentOrder> implements PaymentOrderService {

    @Override
    public PaymentOrder getByPaymentNo(String paymentNo) {
        return lambdaQuery().eq(PaymentOrder::getPaymentNo, paymentNo).one();
    }

    @Override
    public PaymentOrder getByOrderNo(String orderNo) {
        return lambdaQuery().eq(PaymentOrder::getOrderNo, orderNo).one();
    }

    @Override
    public List<PaymentOrder> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(PaymentOrder::getUserId, userId)
                .orderByDesc(PaymentOrder::getCreateTime)
                .list();
    }

    @Override
    public String generatePaymentNo() {
        return "PAY" + IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    public void updatePaymentStatus(Long id, Integer status, String tradeNo) {
        lambdaUpdate()
                .eq(PaymentOrder::getId, id)
                .set(PaymentOrder::getPaymentStatus, status)
                .set(tradeNo != null, PaymentOrder::getTradeNo, tradeNo)
                .update();
    }

    @Override
    public boolean isPaid(String orderNo) {
        PaymentOrder order = getByOrderNo(orderNo);
        return order != null && order.getPaymentStatus() == 2;
    }
}