package com.mall.api.controller.logistics;

import com.mall.api.dto.logistics.ShipDTO;
import com.mall.api.vo.logistics.LogisticsOrderVO;
import com.mall.api.vo.logistics.LogisticsTrackVO;
import com.mall.common.common.Result;
import com.mall.common.exception.BusinessException;
import com.mall.common.security.LoginUser;
import com.mall.logistics.entity.LogisticsOrder;
import com.mall.logistics.entity.LogisticsTrack;
import com.mall.logistics.service.LogisticsOrderService;
import com.mall.logistics.service.LogisticsTrackService;
import com.mall.order.entity.OrderInfo;
import com.mall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "物流管理")
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsOrderService logisticsOrderService;
    private final LogisticsTrackService logisticsTrackService;
    private final OrderService orderService;

    @Operation(summary = "商家发货")
    @PostMapping("/ship")
    public Result<Void> ship(@Valid @RequestBody ShipDTO dto) {
        // 检查订单
        OrderInfo order = orderService.getByOrderNo(dto.getOrderNo());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getOrderStatus().equals(1)) {
            throw new BusinessException("订单状态不允许发货");
        }

        // 创建物流订单
        String logisticsNo = logisticsOrderService.generateLogisticsNo();
        LogisticsOrder logistics = new LogisticsOrder();
        logistics.setLogisticsNo(logisticsNo);
        logistics.setOrderNo(dto.getOrderNo());
        logistics.setUserId(order.getUserId());
        logistics.setLogisticsStatus(0);
        logistics.setLogisticsCompany(dto.getLogisticsCompany());
        logistics.setLogisticsCode(dto.getLogisticsCode());
        logistics.setReceiverName(order.getReceiverName());
        logistics.setReceiverPhone(order.getReceiverPhone());
        logistics.setReceiverProvince(order.getReceiverProvince());
        logistics.setReceiverCity(order.getReceiverCity());
        logistics.setReceiverDistrict(order.getReceiverDistrict());
        logistics.setReceiverDetailAddress(order.getReceiverDetailAddress());
        logisticsOrderService.save(logistics);

        // 更新订单状态为待收货
        orderService.updateStatus(order.getId(), 2);

        // 添加轨迹
        logisticsTrackService.addTrack(logistics.getId(), logisticsNo, 1, "已发货", null);

        return Result.success();
    }

    @Operation(summary = "获取物流信息")
    @GetMapping("/order/{logisticsNo}")
    public Result<LogisticsOrderVO> getLogistics(@AuthenticationPrincipal LoginUser loginUser,
                                                 @PathVariable String logisticsNo) {
        LogisticsOrder logistics = logisticsOrderService.getByLogisticsNo(logisticsNo);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }

        if (!logistics.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("无权查看");
        }

        LogisticsOrderVO vo = convertToVO(logistics);
        return Result.success(vo);
    }

    @Operation(summary = "获取物流轨迹")
    @GetMapping("/track/{logisticsNo}")
    public Result<List<LogisticsTrackVO>> getTrack(@AuthenticationPrincipal LoginUser loginUser,
                                                   @PathVariable String logisticsNo) {
        LogisticsOrder logistics = logisticsOrderService.getByLogisticsNo(logisticsNo);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }

        if (!logistics.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("无权查看");
        }

        List<LogisticsTrack> tracks = logisticsTrackService.getByLogisticsId(logistics.getId());
        List<LogisticsTrackVO> voList = tracks.stream().map(track -> {
            LogisticsTrackVO vo = new LogisticsTrackVO();
            vo.setLogisticsNo(track.getLogisticsNo());
            vo.setStatus(track.getStatus());
            vo.setStatusDesc(track.getStatusDesc());
            vo.setLocation(track.getLocation());
            vo.setTrackTime(track.getTrackTime());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @Operation(summary = "获取用户物流列表")
    @GetMapping("/list")
    public Result<List<LogisticsOrderVO>> getLogisticsList(@AuthenticationPrincipal LoginUser loginUser) {
        List<LogisticsOrder> list = logisticsOrderService.getByUserId(loginUser.getUserId());

        List<LogisticsOrderVO> voList = list.stream().map(logistics -> {
            return convertToVO(logistics);
        }).collect(Collectors.toList());

        return Result.success(voList);
    }

    @Operation(summary = "确认签收")
    @PutMapping("/sign/{logisticsNo}")
    public Result<Void> sign(@AuthenticationPrincipal LoginUser loginUser,
                             @PathVariable String logisticsNo) {
        LogisticsOrder logistics = logisticsOrderService.getByLogisticsNo(logisticsNo);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }

        if (!logistics.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("无权操作");
        }

        if (!logistics.getLogisticsStatus().equals(3)) {
            throw new BusinessException("物流状态不允许签收");
        }

        // 更新物流状态
        logisticsOrderService.sign(logistics.getId());

        // 添加轨迹
        logisticsTrackService.addTrack(logistics.getId(), logisticsNo, 4, "已签收", logistics.getReceiverAddress());

        return Result.success();
    }

    @Operation(summary = "模拟物流状态更新（测试用）")
    @PostMapping("/callback/mock")
    public Result<Void> mockCallback(@RequestParam String logisticsNo,
                                     @RequestParam Integer status,
                                     @RequestParam(required = false) String location) {
        LogisticsOrder logistics = logisticsOrderService.getByLogisticsNo(logisticsNo);
        if (logistics == null) {
            throw new BusinessException("物流信息不存在");
        }

        logisticsOrderService.updateStatus(logistics.getId(), status);

        String statusDesc = getStatusDesc(status);
        logisticsTrackService.addTrack(logistics.getId(), logisticsNo, status, statusDesc, location);

        // 如果是已签收，更新订单状态
        if (status == 4) {
            OrderInfo order = orderService.getByOrderNo(logistics.getOrderNo());
            if (order != null) {
                orderService.updateStatus(order.getId(), 3);
            }
        }

        return Result.success();
    }

    private LogisticsOrderVO convertToVO(LogisticsOrder logistics) {
        LogisticsOrderVO vo = new LogisticsOrderVO();
        vo.setLogisticsNo(logistics.getLogisticsNo());
        vo.setOrderNo(logistics.getOrderNo());
        vo.setLogisticsStatus(logistics.getLogisticsStatus());
        vo.setLogisticsCompany(logistics.getLogisticsCompany());
        vo.setLogisticsCode(logistics.getLogisticsCode());
        vo.setReceiverName(logistics.getReceiverName());
        vo.setReceiverPhone(logistics.getReceiverPhone());
        vo.setReceiverAddress(logistics.getReceiverProvince() + logistics.getReceiverCity() + logistics.getReceiverDistrict() + logistics.getReceiverDetailAddress());
        vo.setDeliveryTime(logistics.getDeliveryTime());
        vo.setSignTime(logistics.getSignTime());
        vo.setCreateTime(logistics.getCreateTime());
        return vo;
    }

    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0: return "待发货";
            case 1: return "已发货";
            case 2: return "运输中";
            case 3: return "待签收";
            case 4: return "已签收";
            case 5: return "拒收";
            case 6: return "退回中";
            case 7: return "已退回";
            default: return "未知状态";
        }
    }
}