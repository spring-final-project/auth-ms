package com.springcloud.demo.authms.monitoring;

import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TracingConfig {

    @Bean
    public AWSXRayServletFilter tracingFilter() {
        return new AWSXRayServletFilter("auth-ms");
    }
}
