package com.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.order.entity.OrderStatusLog;
import com.mall.order.mapper.OrderStatusLogMapper;
import com.mall.order.service.OrderStatusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog> implements OrderStatusLogService {

    @Override
    public void logStatus(Long orderId, Integer status, String changeType, String remark, Long operatorId, String operatorName) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setOrderStatus(status);
        log.setChangeType(changeType);
        log.setRemark(remark);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        save(log);
    }
}