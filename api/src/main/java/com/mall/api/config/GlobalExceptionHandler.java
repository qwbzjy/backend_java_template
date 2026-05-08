package com.mall.api.config;

import com.mall.common.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
@RestControllerAdvice
public class GlobalExceptionHandler extends com.mall.common.exception.GlobalExceptionHandler {
}