package com.mall.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.logistics.entity.LogisticsOrder;

import java.util.List;

public interface LogisticsOrderService extends IService<LogisticsOrder> {

    LogisticsOrder getByLogisticsNo(String logisticsNo);

    LogisticsOrder getByOrderNo(String orderNo);

    List<LogisticsOrder> getByUserId(Long userId);

    String generateLogisticsNo();

    void ship(String orderNo, String logisticsCompany, String logisticsCode);

    void updateStatus(Long id, Integer status);

    void sign(Long id);
}