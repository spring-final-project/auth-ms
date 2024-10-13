package com.springcloud.demo.authms.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springcloud.demo.authms.auth.dto.LoginWithEmailDTO;
import com.springcloud.demo.authms.auth.dto.UserLoggedDTO;
import com.springcloud.demo.authms.auth.service.AuthService;
import com.springcloud.demo.authms.monitoring.TracingExceptions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TracingExceptions tracingExceptions;

    @MockBean
    private AuthService authService;

    @Nested
    class Login {
        @Test
        void login() throws Exception {
            LoginWithEmailDTO loginWithEmailDTO = LoginWithEmailDTO.builder().email("gonzalo@gonzalo.com").password("Abcd1234.").build();
            UserLoggedDTO userLoggedDTO = UserLoggedDTO.builder().email("gonzalo@gonzalo.com").id(UUID.randomUUID().toString()).build();

            given(authService.login(any(LoginWithEmailDTO.class))).willReturn(userLoggedDTO);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(new ObjectMapper().writeValueAsString(loginWithEmailDTO))
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("gonzalo@gonzalo.com"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userLoggedDTO.getId()));

            verify(authService).login(loginWithEmailDTO);
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

            verify(authService, never()).login(any(LoginWithEmailDTO.class));
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

            verify(authService, never()).login(any(LoginWithEmailDTO.class));
        }

        @Test
        void errorWhenNotReceiveBody() throws Exception {

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());

            verify(authService, never()).login(any(LoginWithEmailDTO.class));
        }
    }

}