package com.mall.api.controller.payment;

import com.mall.api.dto.payment.PayOrderDTO;
import com.mall.api.dto.payment.RefundDTO;
import com.mall.api.vo.payment.PaymentOrderVO;
import com.mall.api.vo.payment.PaymentRefundVO;
import com.mall.common.common.Result;
import com.mall.common.exception.BusinessException;
import com.mall.common.security.LoginUser;
import com.mall.order.entity.OrderInfo;
import com.mall.order.service.OrderService;
import com.mall.payment.entity.PaymentOrder;
import com.mall.payment.entity.PaymentRefund;
import com.mall.payment.service.PaymentOrderService;
import com.mall.payment.service.PaymentRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "支付管理")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentOrderService paymentOrderService;
    private final PaymentRefundService paymentRefundService;
    private final OrderService orderService;

    @Operation(summary = "创建支付订单")
    @PostMapping("/create")
    public Result<PaymentOrderVO> createPayment(@AuthenticationPrincipal LoginUser loginUser,
                                                 @Valid @RequestBody PayOrderDTO dto) {
        // 检查订单是否存在且未支付
        OrderInfo order = orderService.getByOrderNo(dto.getOrderNo());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("订单不属于当前用户");
        }

        if (!order.getOrderStatus().equals(0)) {
            throw new BusinessException("订单状态不允许支付");
        }

        // 检查是否已有支付单
        PaymentOrder existPayment = paymentOrderService.getByOrderNo(dto.getOrderNo());
        if (existPayment != null && existPayment.getPaymentStatus() == 2) {
            throw new BusinessException("订单已支付");
        }

        // 创建支付单
        String paymentNo = paymentOrderService.generatePaymentNo();
        PaymentOrder payment = new PaymentOrder();
        payment.setPaymentNo(paymentNo);
        payment.setOrderNo(dto.getOrderNo());
        payment.setUserId(loginUser.getUserId());
        payment.setAmount(order.getPayAmount());
        payment.setPaymentStatus(0); // 待支付
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟过期
        paymentOrderService.save(payment);

        PaymentOrderVO vo = new PaymentOrderVO();
        BeanUtils.copyProperties(payment, vo);
        return Result.success(vo);
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/status/{orderNo}")
    public Result<PaymentOrderVO> getPaymentStatus(@AuthenticationPrincipal LoginUser loginUser,
                                                   @PathVariable String orderNo) {
        PaymentOrder payment = paymentOrderService.getByOrderNo(orderNo);
        if (payment == null) {
            throw new BusinessException("支付订单不存在");
        }

        if (!payment.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("无权查看");
        }

        PaymentOrderVO vo = new PaymentOrderVO();
        BeanUtils.copyProperties(payment, vo);
        return Result.success(vo);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund")
    public Result<PaymentRefundVO> applyRefund(@AuthenticationPrincipal LoginUser loginUser,
                                                @Valid @RequestBody RefundDTO dto) {
        // 检查订单
        OrderInfo order = orderService.getByOrderNo(dto.getOrderNo());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("订单不属于当前用户");
        }

        // 检查支付状态
        PaymentOrder payment = paymentOrderService.getByOrderNo(dto.getOrderNo());
        if (payment == null || payment.getPaymentStatus() != 2) {
            throw new BusinessException("订单未支付，无法退款");
        }

        // 检查退款金额
        if (dto.getRefundAmount().compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("退款金额超过支付金额");
        }

        // 创建退款记录
        paymentRefundService.createRefund(
                payment.getPaymentNo(),
                dto.getOrderNo(),
                loginUser.getUserId(),
                dto.getRefundAmount(),
                dto.getRefundReason()
        );

        // TODO: 调用第三方支付平台退款接口

        PaymentRefundVO vo = new PaymentRefundVO();
        return Result.success(vo);
    }

    @Operation(summary = "查询退款状态")
    @GetMapping("/refund/{refundNo}")
    public Result<PaymentRefundVO> getRefundStatus(@AuthenticationPrincipal LoginUser loginUser,
                                                  @PathVariable String refundNo) {
        PaymentRefund refund = paymentRefundService.getByRefundNo(refundNo);
        if (refund == null) {
            throw new BusinessException("退款记录不存在");
        }

        if (!refund.getUserId().equals(loginUser.getUserId())) {
            throw new BusinessException("无权查看");
        }

        PaymentRefundVO vo = new PaymentRefundVO();
        BeanUtils.copyProperties(refund, vo);
        return Result.success(vo);
    }

    @Operation(summary = "模拟支付回调（测试用）")
    @PostMapping("/callback/mock")
    public Result<Void> mockCallback(@RequestParam String paymentNo,
                                      @RequestParam(defaultValue = "success") String status) {
        PaymentOrder payment = paymentOrderService.getByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException("支付订单不存在");
        }

        if ("success".equals(status)) {
            paymentOrderService.updatePaymentStatus(payment.getId(), 2, "MOCK_" + paymentNo);

            // 更新订单状态为待发货
            OrderInfo order = orderService.getByOrderNo(payment.getOrderNo());
            if (order != null) {
                orderService.updateStatus(order.getId(), 1);
            }
        } else {
            paymentOrderService.updatePaymentStatus(payment.getId(), 3, null);
        }

        return Result.success();
    }

    @Operation(summary = "获取用户支付记录")
    @GetMapping("/list")
    public Result<List<PaymentOrderVO>> getPaymentList(@AuthenticationPrincipal LoginUser loginUser) {
        List<PaymentOrder> payments = paymentOrderService.getByUserId(loginUser.getUserId());

        List<PaymentOrderVO> voList = payments.stream().map(p -> {
            PaymentOrderVO vo = new PaymentOrderVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(voList);
    }
}