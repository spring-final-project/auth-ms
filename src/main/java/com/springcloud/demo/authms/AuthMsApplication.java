package com.springcloud.demo.authms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuthMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthMsApplication.class, args);
	}

}
