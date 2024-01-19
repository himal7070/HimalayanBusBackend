package com.himalayanbus.controller;


import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.exception.InvalidCredentialsException;
import com.himalayanbus.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/himalayanbus")
@CrossOrigin(origins = {"http://localhost:5173"})
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authResponse);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    @PostMapping("/loginWithGoogle")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestParam String googleUserEmail) {
        AuthResponse authResponse = authService.loginWithGoogle(googleUserEmail);
        return ResponseEntity.ok(authResponse);
    }




}
