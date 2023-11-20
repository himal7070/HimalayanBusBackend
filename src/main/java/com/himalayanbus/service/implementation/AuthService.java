package com.himalayanbus.service.implementation;


import com.himalayanbus.dtos.AuthResponse;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IAdminRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessTokenEncoder;
import com.himalayanbus.security.token.impl.AccessTokenImpl;
import com.himalayanbus.service.IAuthService;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private final IAdminRepository adminRepository;
    private final AccessTokenEncoder accessTokenEncoder;

    public AuthService(IUserRepository userRepository, IAdminRepository adminRepository, AccessTokenEncoder accessTokenEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.accessTokenEncoder = accessTokenEncoder;
    }



    @Override
    public AuthResponse login(String email, String password) throws AuthenticationException {
        User user = userRepository.findByEmail(email);
        Admin admin = adminRepository.findByEmail(email);

        if (user == null && admin == null) {
            throw new AuthenticationException("User not found");
        }

        if ((user != null && matchesPassword(user.getPassword(), password)) ||
                (admin != null && matchesPassword(admin.getPassword(), password))) {
            throw new AuthenticationException("Invalid credentials");
        }

        String username;
        Set<Role> roles;
        Integer entityId;
        if (user != null) {
            username = user.getUserName();
            roles = user.getRoles();
            entityId = user.getUserID();
        } else {
            username = admin.getUserName();
            roles = admin.getRoles();
            entityId = admin.getAdminID();
        }

        String token = generateTokenForRoles(username, roles, entityId);
        String roleString = getRolesAsString(roles);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(roleString);
        return response;
    }

    private boolean matchesPassword(String storedPassword, String enteredPassword) {
        return !storedPassword.equals(enteredPassword);
    }

    private String generateTokenForRoles(String username, Set<Role> roles, Integer entityId) {
        List<String> roleNames = roles.stream()
                .map(role -> role.getRole().toString())
                .toList();

        AccessTokenImpl accessToken = new AccessTokenImpl(username, entityId, roleNames);
        return accessTokenEncoder.encode(accessToken);
    }


    private String getRolesAsString(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRole().toString())
                .collect(Collectors.joining(","));
    }






}
