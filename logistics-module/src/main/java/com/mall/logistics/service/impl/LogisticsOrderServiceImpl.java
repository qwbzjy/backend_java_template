package com.mall.logistics.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.logistics.entity.LogisticsOrder;
import com.mall.logistics.mapper.LogisticsOrderMapper;
import com.mall.logistics.service.LogisticsOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogisticsOrderServiceImpl extends ServiceImpl<LogisticsOrderMapper, LogisticsOrder> implements LogisticsOrderService {

    @Override
    public LogisticsOrder getByLogisticsNo(String logisticsNo) {
        return lambdaQuery().eq(LogisticsOrder::getLogisticsNo, logisticsNo).one();
    }

    @Override
    public LogisticsOrder getByOrderNo(String orderNo) {
        return lambdaQuery().eq(LogisticsOrder::getOrderNo, orderNo).one();
    }

    @Override
    public List<LogisticsOrder> getByUserId(Long userId) {
        return lambdaQuery()
                .eq(LogisticsOrder::getUserId, userId)
                .orderByDesc(LogisticsOrder::getCreateTime)
                .list();
    }

    @Override
    public String generateLogisticsNo() {
        return "EXP" + IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    public void ship(String orderNo, String logisticsCompany, String logisticsCode) {
        LogisticsOrder order = getByOrderNo(orderNo);
        if (order != null) {
            order.setLogisticsNo(generateLogisticsNo());
            order.setLogisticsCompany(logisticsCompany);
            order.setLogisticsCode(logisticsCode);
            order.setLogisticsStatus(1); // 已发货
            order.setDeliveryTime(LocalDateTime.now());
            updateById(order);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        lambdaUpdate()
                .eq(LogisticsOrder::getId, id)
                .set(LogisticsOrder::getLogisticsStatus, status)
                .update();
    }

    @Override
    public void sign(Long id) {
        lambdaUpdate()
                .eq(LogisticsOrder::getId, id)
                .set(LogisticsOrder::getLogisticsStatus, 4) // 已签收
                .set(LogisticsOrder::getSignTime, LocalDateTime.now())
                .update();
    }
}