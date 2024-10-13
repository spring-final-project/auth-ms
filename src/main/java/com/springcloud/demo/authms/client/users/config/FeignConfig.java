package com.springcloud.demo.authms.client.users.config;

import com.springcloud.demo.authms.monitoring.XRayFeignInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor xRayFeignInterceptor() {
        return new XRayFeignInterceptor();
    }
}
