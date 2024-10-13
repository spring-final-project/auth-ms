package com.springcloud.demo.authms.auth.service;

import com.springcloud.demo.authms.auth.dto.LoginWithEmailDTO;
import com.springcloud.demo.authms.auth.dto.UserLoggedDTO;
import com.springcloud.demo.authms.client.users.UserClientImpl;
import com.springcloud.demo.authms.client.users.dto.UserDTO;

import static org.assertj.core.api.Assertions.*;

import com.springcloud.demo.authms.exceptions.BadRequestException;
import com.springcloud.demo.authms.exceptions.ForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.BDDMockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserClientImpl userClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    class LoginWithEmail {
        @Test
        void loginWithEmail() {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password("123456").build();
            UserDTO userDTO = UserDTO.builder().id(UUID.randomUUID()).email("gonzalo@gonzalo.com").password("password_encrypted").build();

            given(userClient.findByEmail(anyString())).willReturn(userDTO);
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

            UserLoggedDTO response = authService.login(loginWithEmailDTO);

            verify(userClient).findByEmail("gonzalo@gonzalo.com");
            verify(passwordEncoder).matches("123456", userDTO.getPassword());

            assertThat(response.getEmail()).isEqualTo(userDTO.getEmail());
            assertThat(response.getId()).isEqualTo(userDTO.getId().toString());
        }

        @Test
        void errorWhenPasswordNotMatch() {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password("123456").build();
            UserDTO userDTO = UserDTO.builder().id(UUID.randomUUID()).email("gonzalo@gonzalo.com").password("password_encrypted").build();

            given(userClient.findByEmail(anyString())).willReturn(userDTO);
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            BadRequestException response = Assertions.assertThrows(BadRequestException.class, () -> authService.login(loginWithEmailDTO));

            verify(userClient).findByEmail("gonzalo@gonzalo.com");
            verify(passwordEncoder).matches("123456", userDTO.getPassword());

            assertThat(response.getMessage()).contains("Invalid credentials");
        }

        @Test
        void errorWhenCannotConnectToUserService() {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password("123456").build();

            given(userClient.findByEmail(anyString())).willThrow(new ForbiddenException("Users service not available. Try later"));

            ForbiddenException response = Assertions.assertThrows(ForbiddenException.class, () -> authService.login(loginWithEmailDTO));

            verify(userClient).findByEmail("gonzalo@gonzalo.com");
            verify(passwordEncoder, never()).matches(anyString(), anyString());

            assertThat(response.getMessage()).contains("Users service not available. Try later");
        }
    }

}