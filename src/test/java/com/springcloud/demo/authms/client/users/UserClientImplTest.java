package com.springcloud.demo.authms.client.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.demo.authms.client.users.dto.UserDTO;

import static org.assertj.core.api.Assertions.*;

import com.springcloud.demo.authms.exceptions.ForbiddenException;
import com.springcloud.demo.authms.exceptions.InheritedException;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Map;
import java.util.UUID;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class UserClientImplTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserClientImpl userClientImpl;

    @Nested
    class FindUserByEmail {
        @Test
        void findUserByEmail() {
            UserDTO userDTO = UserDTO.builder().id(UUID.randomUUID()).email("test@test.com").password("encrypted_password").build();
            given(userClient.findByEmail("test@test.com")).willReturn(userDTO);

            UserDTO result = userClientImpl.findByEmail("test@test.com");

            assertThat(result).isEqualTo(userDTO);
        }
    }

    @Nested
    class Fallback {
        @Test
        void whenCannotConnectToUsersService() {

            ForbiddenException response = Assertions.assertThrows(ForbiddenException.class, () -> {
                userClientImpl.findUserByEmailFallback("test@test.com", new RuntimeException());
            });

            assertThat(response.getMessage()).isEqualTo("Users service not available. Try later");
        }

        @Test
        void whenReceiveClientExceptionFromUsersService() throws JsonProcessingException {
            Map body = Map.of("message", "Not found user with email");
            String bodyString = new ObjectMapper().writeValueAsString(body);

            InheritedException response = Assertions.assertThrows(InheritedException.class, () -> {
                userClientImpl.findUserByEmailFallback(
                        "test@test.com",
                        new FeignException.FeignClientException(400, null, mock(Request.class), bodyString.getBytes(), null)
                );
            });

            assertThat(response.getMessage()).isEqualTo(body.get("message"));
        }
    }

}