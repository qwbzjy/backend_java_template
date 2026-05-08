package com.mall.common.exception;

public enum ErrorCode {

    // 成功
    SUCCESS(200, "success"),

    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 服务端错误
    INTERNAL_ERROR(500, "系统异常"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误 - 用户模块 (1001-1999)
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    PASSWORD_FORMAT_ERROR(1004, "密码格式错误"),
    PHONE_ALREADY_EXISTS(1005, "手机号已被注册"),
    EMAIL_ALREADY_EXISTS(1006, "邮箱已被注册"),
    VERIFY_CODE_ERROR(1007, "验证码错误"),
    VERIFY_CODE_EXPIRED(1008, "验证码已过期"),

    // 业务错误 - 商品模块 (2001-2999)
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OFF_SHELF(2002, "商品已下架"),
    PRODUCT_STOCK_NOT_ENOUGH(2003, "库存不足"),
    CATEGORY_NOT_FOUND(2004, "分类不存在"),

    // 业务错误 - 订单模块 (3001-3999)
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_PRICE_CHANGED(3003, "商品价格已变动"),
    CART_EMPTY(3004, "购物车为空"),

    // 业务错误 - 支付模块 (4001-4999)
    PAYMENT_FAILED(4001, "支付失败"),
    PAYMENT_TIMEOUT(4002, "支付超时"),
    PAYMENT_AMOUNT_ERROR(4003, "支付金额错误"),
    REFUND_FAILED(4004, "退款失败"),

    // 业务错误 - 物流模块 (5001-5999)
    LOGISTICS_NOT_FOUND(5001, "物流信息不存在"),
    LOGISTICS_COMPANY_NOT_FOUND(5002, "物流公司不存在");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}