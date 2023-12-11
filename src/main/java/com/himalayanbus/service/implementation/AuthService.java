package com.himalayanbus.service.implementation;


import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.exception.InvalidCredentialsException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessTokenEncoder;
import com.himalayanbus.security.token.impl.AccessTokenImpl;
import com.himalayanbus.service.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final AccessTokenEncoder accessTokenEncoder;
    private final PasswordEncoder passwordEncoder;


    public AuthService(IUserRepository userRepository, AccessTokenEncoder accessTokenEncoder, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accessTokenEncoder = accessTokenEncoder;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !matchesPassword(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = generateAccessToken(user);
        return new AuthResponse(accessToken);
    }



    private boolean matchesPassword(String rawPassword, String storedHashedPassword) {
        return passwordEncoder.matches(rawPassword, storedHashedPassword);
    }

    private String generateAccessToken(User user) {
        Long passengerId = user.getPassenger() != null ? user.getPassenger().getPassengerId() : null;
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().toString())
                .toList();

        return accessTokenEncoder.encode(
                new AccessTokenImpl(user.getEmail(), passengerId, roles));
    }








}
