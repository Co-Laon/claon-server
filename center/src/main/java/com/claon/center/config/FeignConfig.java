package com.claon.center.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.claon.center")
@Configuration
public class FeignConfig {
}
