package com.himalayanbus.service;

import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import org.springframework.transaction.annotation.Transactional;

public interface IAuthService {
    @Transactional
    AuthResponse login(LoginRequest loginRequest);

    @Transactional
    AuthResponse loginWithGoogle(String googleUserEmail);
}
