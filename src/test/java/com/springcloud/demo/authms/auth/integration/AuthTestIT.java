package com.springcloud.demo.authms.auth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.demo.authms.auth.dto.LoginWithEmailDTO;
import com.springcloud.demo.authms.client.users.UserClientImpl;
import com.springcloud.demo.authms.client.users.dto.UserDTO;
import com.springcloud.demo.authms.exceptions.ForbiddenException;
import com.springcloud.demo.authms.exceptions.InheritedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserClientImpl userClient;

    @Nested
    class Login {
        @Test
        void login() throws Exception {
            String password = "Abcd1234.";
            String encodedPassword = passwordEncoder.encode(password);
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password(password).build();
            UserDTO userDTO = UserDTO.builder().email("gonzalo@gonzalo.com").password(encodedPassword).id(UUID.randomUUID()).build();

            given(userClient.findByEmail(anyString())).willReturn(userDTO);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(loginWithEmailDTO.getEmail()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDTO.getId().toString()));
        }

        @Test
        void errorWhenMissingFields() throws Exception {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().build();

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(2));
        }

        @Test
        void errorWhenInvalidFields() throws Exception {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo").password("123456").build();

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors.size()").value(2));
        }

        @Test
        void errorWhenNotFoundUserWithEmail() throws Exception {
            String password = "Abcd1234.";
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password(password).build();

            given(userClient.findByEmail(anyString())).willThrow(new InheritedException(HttpStatus.BAD_REQUEST.value(), "User not found"));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User not found"));

        }

        @Test
        void errorWhenInvalidCredentials() throws Exception {
            String password = "Abcd1234.";
            String encodedPassword = passwordEncoder.encode(password);
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password("Abcd123456!").build();
            UserDTO userDTO = UserDTO.builder().email("gonzalo@gonzalo.com").password(encodedPassword).id(UUID.randomUUID()).build();

            given(userClient.findByEmail(anyString())).willReturn(userDTO);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid credentials"));

        }

        @Test
        void errorWhenCannotConnectToUserService() throws Exception {
            String password = "Abcd1234.";
            String encodedPassword = passwordEncoder.encode(password);
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password(password).build();
            UserDTO userDTO = UserDTO.builder().email("gonzalo@gonzalo.com").password(encodedPassword).id(UUID.randomUUID()).build();

            given(userClient.findByEmail(anyString())).willThrow(new ForbiddenException("Users service not available. Try later"));

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Users service not available. Try later"));

        }
    }
}
