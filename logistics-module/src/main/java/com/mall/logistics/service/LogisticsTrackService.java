package com.mall.logistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.logistics.entity.LogisticsTrack;

import java.util.List;

public interface LogisticsTrackService extends IService<LogisticsTrack> {

    List<LogisticsTrack> getByLogisticsId(Long logisticsId);

    void addTrack(Long logisticsId, String logisticsNo, Integer status, String statusDesc, String location);
}