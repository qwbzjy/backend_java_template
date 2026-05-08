package com.mall.logistics.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.logistics.entity.LogisticsTrack;
import com.mall.logistics.mapper.LogisticsTrackMapper;
import com.mall.logistics.service.LogisticsTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogisticsTrackServiceImpl extends ServiceImpl<LogisticsTrackMapper, LogisticsTrack> implements LogisticsTrackService {

    @Override
    public List<LogisticsTrack> getByLogisticsId(Long logisticsId) {
        return lambdaQuery()
                .eq(LogisticsTrack::getLogisticsId, logisticsId)
                .orderByAsc(LogisticsTrack::getTrackTime)
                .list();
    }

    @Override
    public void addTrack(Long logisticsId, String logisticsNo, Integer status, String statusDesc, String location) {
        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logisticsId);
        track.setLogisticsNo(logisticsNo);
        track.setStatus(status);
        track.setStatusDesc(statusDesc);
        track.setLocation(location);
        track.setTrackTime(LocalDateTime.now());
        save(track);
    }
}