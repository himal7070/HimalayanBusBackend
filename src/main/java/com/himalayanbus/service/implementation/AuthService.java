package com.himalayanbus.service.implementation;


import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.exception.InvalidCredentialsException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessTokenEncoder;
import com.himalayanbus.security.token.impl.AccessTokenImpl;
import com.himalayanbus.service.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final AccessTokenEncoder accessTokenEncoder;
    private final PasswordEncoder passwordEncoder;
    private final IRoleRepository roleRepository;


    public AuthService(IUserRepository userRepository, AccessTokenEncoder accessTokenEncoder, PasswordEncoder passwordEncoder, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.accessTokenEncoder = accessTokenEncoder;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user != null && user.getResetToken() != null && user.getResetTokenExpiry() != null) {

            throw new InvalidCredentialsException();
        }

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
        Long userID = user.getUserID();
        List<String> roles = user.getRoles().stream()
                .map(userRole -> userRole.getRole().toString())
                .toList();

        return accessTokenEncoder.encode(
                new AccessTokenImpl(user.getEmail(), userID, roles));

    }


    @Override
    @Transactional
    public AuthResponse loginWithGoogle(String googleUserEmail) {
        User user = userRepository.findByEmail(googleUserEmail);

        if (user == null) {
            user = createUserFromGoogle(googleUserEmail);
        }

        String accessToken = generateAccessToken(user);
        return new AuthResponse(accessToken);
    }

    User createUserFromGoogle(String googleUserEmail) {

        User existingUser = userRepository.findByEmail(googleUserEmail);
        if (existingUser != null) {
            return existingUser;
        }

        Role userRole = roleRepository.findByRole(UserRole.USER);

        if (userRole == null) {
            userRole = new Role();
            userRole.setRole(UserRole.USER);
            roleRepository.save(userRole);
        }

        User newUser = new User();
        newUser.setEmail(googleUserEmail);

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);

        userRepository.save(newUser);

        return newUser;
    }







}
