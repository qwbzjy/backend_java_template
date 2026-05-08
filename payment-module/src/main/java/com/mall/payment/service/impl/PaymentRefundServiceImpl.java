package com.mall.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.payment.entity.PaymentRefund;
import com.mall.payment.mapper.PaymentRefundMapper;
import com.mall.payment.service.PaymentRefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentRefundServiceImpl extends ServiceImpl<PaymentRefundMapper, PaymentRefund> implements PaymentRefundService {

    @Override
    public PaymentRefund getByRefundNo(String refundNo) {
        return lambdaQuery().eq(PaymentRefund::getRefundNo, refundNo).one();
    }

    @Override
    public String generateRefundNo() {
        return "REF" + IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    public void createRefund(String paymentNo, String orderNo, Long userId, BigDecimal refundAmount, String refundReason) {
        PaymentRefund refund = new PaymentRefund();
        refund.setRefundNo(generateRefundNo());
        refund.setPaymentNo(paymentNo);
        refund.setOrderNo(orderNo);
        refund.setUserId(userId);
        refund.setRefundAmount(refundAmount);
        refund.setRefundReason(refundReason);
        refund.setRefundStatus(0);
        save(refund);
    }

    @Override
    public void updateRefundStatus(Long id, Integer status, String tradeNo) {
        lambdaUpdate()
                .eq(PaymentRefund::getId, id)
                .set(PaymentRefund::getRefundStatus, status)
                .set(tradeNo != null, PaymentRefund::getTradeNo, tradeNo)
                .update();
    }
}