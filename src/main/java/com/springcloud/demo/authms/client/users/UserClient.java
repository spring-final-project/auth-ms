package com.springcloud.demo.authms.client.users;

import com.springcloud.demo.authms.client.users.config.FeignConfig;
import com.springcloud.demo.authms.client.users.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-ms", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/users/email/{email}")
    UserDTO findByEmail(@PathVariable String email);
}
