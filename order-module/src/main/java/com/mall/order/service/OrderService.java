package com.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.order.entity.OrderInfo;

import java.util.List;

public interface OrderService extends IService<OrderInfo> {

    OrderInfo getByOrderNo(String orderNo);

    List<OrderInfo> getByUserId(Long userId);

    String generateOrderNo();

    void saveOrderWithItems(OrderInfo order, List<OrderInfo> items);

    void updateStatus(Long orderId, Integer status);
}