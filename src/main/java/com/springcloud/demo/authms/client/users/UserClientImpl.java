package com.springcloud.demo.authms.client.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.demo.authms.client.users.dto.UserDTO;
import com.springcloud.demo.authms.exceptions.ForbiddenException;
import com.springcloud.demo.authms.exceptions.InheritedException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final UserClient userClient;

    @Override
    @CircuitBreaker(name = "users-service", fallbackMethod = "findUserByEmailFallback")
    public UserDTO findByEmail(String email) {
        return userClient.findByEmail(email);
    }

    UserDTO findUserByEmailFallback(String email, Throwable e) throws JsonProcessingException {
        if (!(e instanceof FeignException.FeignClientException feignClientException)) {
            throw new ForbiddenException("Users service not available. Try later");
        }

        Map body = new ObjectMapper().readValue(feignClientException.contentUTF8(), Map.class);

        throw new InheritedException(
                feignClientException.status(),
                (String) body.get("message")
        );
    }
}
