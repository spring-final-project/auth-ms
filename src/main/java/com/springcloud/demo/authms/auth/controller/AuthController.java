package com.springcloud.demo.authms.auth.controller;

import com.springcloud.demo.authms.auth.dto.LoginWithEmailDTO;
import com.springcloud.demo.authms.auth.dto.UserLoggedDTO;
import com.springcloud.demo.authms.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public UserLoggedDTO login(@Valid @RequestBody LoginWithEmailDTO loginWithEmailDTO, HttpServletResponse response){
        UserLoggedDTO userLoggedDTO = authService.login(loginWithEmailDTO);
        response.setHeader("email", userLoggedDTO.getEmail());
        return userLoggedDTO;
    }
}
