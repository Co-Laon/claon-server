package com.claon.user.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.claon.user")
@Configuration
public class FeignConfig {
}
