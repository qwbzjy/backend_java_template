package com.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.order.entity.OrderInfo;
import com.mall.order.entity.OrderItem;
import com.mall.order.mapper.OrderInfoMapper;
import com.mall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        return lambdaQuery().eq(OrderInfo::getOrderNo, orderNo).one();
    }

    @Override
    public List<OrderInfo> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(OrderInfo::getUserId, userId)
                .orderByDesc(OrderInfo::getCreateTime)
                .list();
    }

    @Override
    public String generateOrderNo() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderWithItems(OrderInfo order, List<OrderItem> items) {
        save(order);
        items.forEach(item -> item.setOrderId(order.getId()));
        // 批量保存订单项
    }

    @Override
    public void updateStatus(Long orderId, Integer status) {
        lambdaUpdate()
                .eq(OrderInfo::getId, orderId)
                .set(OrderInfo::getOrderStatus, status)
                .update();
    }
}