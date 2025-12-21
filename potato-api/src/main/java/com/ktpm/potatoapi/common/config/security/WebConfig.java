package com.ktpm.potatoapi.common.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Áp dụng cho tất cả endpoint
                .allowedOrigins("*") // Cho phép tất cả domain
                .allowedMethods("*") // Cho phép tất cả method: GET, POST, PUT, DELETE, PATCH, OPTIONS
                .allowedHeaders("*"); // Cho phép tất cả headers
    }
}
