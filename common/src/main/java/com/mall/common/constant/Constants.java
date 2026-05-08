package com.mall.common.constant;

/**
 * 通用常量
 */
public class Constants {

    private Constants() {}

    // 分页默认值
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Redis key 前缀
    public static final String REDIS_KEY_PREFIX = "mall:";
    public static final String REDIS_KEY_USER = REDIS_KEY_PREFIX + "user:";
    public static final String REDIS_KEY_TOKEN = REDIS_KEY_PREFIX + "token:";
    public static final String REDIS_KEY_CART = REDIS_KEY_PREFIX + "cart:";
    public static final String REDIS_KEY_PRODUCT = REDIS_KEY_PREFIX + "product:";
    public static final String REDIS_KEY_STOCK = REDIS_KEY_PREFIX + "stock:";
    public static final String REDIS_KEY_LOCK = REDIS_KEY_PREFIX + "lock:";

    // JWT 相关
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    // 日期格式
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
}