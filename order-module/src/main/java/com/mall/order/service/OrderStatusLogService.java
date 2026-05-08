package com.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.order.entity.OrderStatusLog;

public interface OrderStatusLogService extends IService<OrderStatusLog> {

    void logStatus(Long orderId, Integer status, String changeType, String remark, Long operatorId, String operatorName);
}