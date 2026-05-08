package com.mall.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class RedisConfig extends com.mall.common.config.RedisConfig {

    public RedisConfig(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
    }
}