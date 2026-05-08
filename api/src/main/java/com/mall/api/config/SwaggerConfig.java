package com.mall.api.config;

import com.mall.common.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig extends com.mall.common.config.SwaggerConfig {

    @Override
    @Bean
    public OpenAPI openAPI() {
        return super.openAPI();
    }
}