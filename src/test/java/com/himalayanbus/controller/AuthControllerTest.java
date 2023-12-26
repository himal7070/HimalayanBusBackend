package com.himalayanbus.controller;

import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.exception.InvalidCredentialsException;
import com.himalayanbus.service.IAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginSuccess() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("himal@aryal.nl", "password");
        AuthResponse expectedResponse = new AuthResponse("access-token");
        when(authService.login(any())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<AuthResponse> responseEntity = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testLoginFailure() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalid@aryal.nl", "wrong password");
        when(authService.login(any())).thenThrow(new InvalidCredentialsException());

        // Act
        ResponseEntity<AuthResponse> responseEntity = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

}