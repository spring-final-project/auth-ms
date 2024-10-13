package com.springcloud.demo.authms.auth.service;

import com.springcloud.demo.authms.auth.dto.LoginWithEmailDTO;
import com.springcloud.demo.authms.auth.dto.UserLoggedDTO;
import com.springcloud.demo.authms.client.users.UserClientImpl;
import com.springcloud.demo.authms.client.users.dto.UserDTO;
import com.springcloud.demo.authms.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClientImpl userClient;
    private final PasswordEncoder passwordEncoder;

    public UserLoggedDTO login(LoginWithEmailDTO loginWithEmailDTO) {
        UserDTO user = userClient.findByEmail(loginWithEmailDTO.getEmail());

        boolean isValidPassword = passwordEncoder.matches(loginWithEmailDTO.getPassword(), user.getPassword());

        if(!isValidPassword){
            throw new BadRequestException("Invalid credentials");
        }

        return UserLoggedDTO.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .build();
    }
}
