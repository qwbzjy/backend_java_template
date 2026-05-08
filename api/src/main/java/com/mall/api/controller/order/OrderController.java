package com.mall.api.controller.order;

import com.mall.api.dto.order.CreateOrderDTO;
import com.mall.api.vo.order.OrderItemVO;
import com.mall.api.vo.order.OrderVO;
import com.mall.common.common.PageResult;
import com.mall.common.common.Result;
import com.mall.common.exception.BusinessException;
import com.mall.common.exception.ErrorCode;
import com.mall.common.security.LoginUser;
import com.mall.order.entity.OrderInfo;
import com.mall.order.entity.OrderItem;
import com.mall.order.service.OrderItemService;
import com.mall.order.service.OrderService;
import com.mall.order.service.OrderStatusLogService;
import com.mall.product.entity.ProductSku;
import com.mall.product.service.ProductSkuService;
import com.mall.product.service.ProductStockService;
import com.mall.user.entity.UserAddress;
import com.mall.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final OrderStatusLogService orderStatusLogService;
    private final UserAddressService userAddressService;
    private final ProductSkuService productSkuService;
    private final ProductStockService productStockService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(@AuthenticationPrincipal LoginUser loginUser,
                                       @Valid @RequestBody CreateOrderDTO dto) {
        // 获取收货地址
        UserAddress address = userAddressService.getById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("收货地址不存在");
        }

        // 锁定库存并构建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            ProductSku sku = productSkuService.getById(itemDTO.getSkuId());
            if (sku == null || sku.getStatus() != 1) {
                throw new BusinessException("商品不存在或已下架: " + itemDTO.getSkuId());
            }

            // 锁定库存
            boolean locked = productStockService.lockStock(sku.getId(), itemDTO.getQuantity());
            if (!locked) {
                throw new BusinessException("库存不足: " + sku.getSkuCode());
            }

            // 构建订单项（快照）
            OrderItem item = new OrderItem();
            item.setSkuId(sku.getId());
            item.setSkuCode(sku.getSkuCode());
            item.setSkuName(""); // TODO: 从SPU获取
            item.setSkuImage(sku.getImage());
            item.setSpecs(sku.getSpecs());
            item.setPrice(sku.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setTotalAmount(sku.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItems.add(item);

            totalAmount = totalAmount.add(item.getTotalAmount());
        }

        // 生成订单
        String orderNo = orderService.generateOrderNo();
        OrderInfo order = new OrderInfo();
        order.setOrderNo(orderNo);
        order.setUserId(loginUser.getUserId());
        order.setOrderStatus(0); // 待付款
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverDetailAddress(address.getDetailAddress());
        order.setBuyerRemark(dto.getBuyerRemark());

        orderService.save(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemService.save(item);
        }

        // 记录状态日志
        orderStatusLogService.logStatus(order.getId(), 0, "USER", "创建订单", loginUser.getUserId(), loginUser.getUsername());

        OrderVO vo = convertToVO(order, orderItems);
        return Result.success(vo);
    }

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageResult<OrderVO>> getOrderList(@AuthenticationPrincipal LoginUser loginUser,
                                                    @RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(required = false) Integer status) {
        List<OrderInfo> orders = orderService.getByUserId(loginUser.getUserId());

        // 按状态过滤
        if (status != null) {
            orders = orders.stream()
                    .filter(o -> o.getOrderStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // 分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, orders.size());
        List<OrderInfo> pageList = orders.subList(start, end);

        // 转换为VO
        List<OrderVO> voList = pageList.stream().map(order -> {
            List<OrderItem> items = orderItemService.getByOrderId(order.getId());
            return convertToVO(order, items);
        }).collect(Collectors.toList());

        PageResult<OrderVO> pageResult = PageResult.of(orders.size(), pageNum, pageSize, voList);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(@AuthenticationPrincipal LoginUser loginUser,
                                         @PathVariable Long id) {
        OrderInfo order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("订单不存在");
        }

        List<OrderItem> items = orderItemService.getByOrderId(id);
        return Result.success(convertToVO(order, items));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@AuthenticationPrincipal LoginUser loginUser,
                                      @PathVariable Long id) {
        OrderInfo order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("订单不存在");
        }

        if (order.getOrderStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }

        // 解锁库存
        List<OrderItem> items = orderItemService.getByOrderId(id);
        for (OrderItem item : items) {
            productStockService.unlockStock(item.getSkuId(), item.getQuantity());
        }

        // 更新订单状态
        orderService.updateStatus(id, 4);
        orderStatusLogService.logStatus(id, 4, "USER", "用户取消订单", loginUser.getUserId(), loginUser.getUsername());

        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/receive")
    public Result<Void> receiveOrder(@AuthenticationPrincipal LoginUser loginUser,
                                      @PathVariable Long id) {
        OrderInfo order = orderService.getById(id);
        if (order == null || !order.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("订单不存在");
        }

        if (order.getOrderStatus() != 2) {
            throw new BusinessException("订单状态不允许确认收货");
        }

        // 扣减库存（从锁定转为已售）
        List<OrderItem> items = orderItemService.getByOrderId(id);
        for (OrderItem item : items) {
            productStockService.deductStock(item.getSkuId(), item.getQuantity());
        }

        // 更新订单状态
        orderService.updateStatus(id, 3);
        orderStatusLogService.logStatus(id, 3, "USER", "确认收货", loginUser.getUserId(), loginUser.getUsername());

        return Result.success();
    }

    private OrderVO convertToVO(OrderInfo order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setPayTime(order.getPayTime());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setReceiveTime(order.getReceiveTime());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverProvince() + order.getReceiverCity() + order.getReceiverDistrict() + order.getReceiverDetailAddress());
        vo.setBuyerRemark(order.getBuyerRemark());
        vo.setCreateTime(order.getCreateTime());

        List<OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderItemVO itemVO = new OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setSkuId(item.getSkuId());
            itemVO.setSkuCode(item.getSkuCode());
            itemVO.setSkuName(item.getSkuName());
            itemVO.setSkuImage(item.getSkuImage());
            itemVO.setSpecs(item.getSpecs());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalAmount(item.getTotalAmount());
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }
}