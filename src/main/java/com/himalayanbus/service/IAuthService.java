package com.himalayanbus.service;

import com.himalayanbus.dtos.AuthResponse;

import javax.security.sasl.AuthenticationException;

public interface IAuthService {

    AuthResponse login(String email, String password) throws AuthenticationException;
}
