package com.himalayanbus.controller;


import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.service.IAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;


@RestController
@RequestMapping("/himalayanbus")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) throws AuthenticationException {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

}
