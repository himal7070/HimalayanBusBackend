package com.himalayanbus.service.implementation;

import com.himalayanbus.dtos.LoginRequest;
import com.himalayanbus.exception.AdminException;
import com.himalayanbus.exception.InvalidCredentialsException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessTokenEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.himalayanbus.dtos.AuthResponse;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private AccessTokenEncoder accessTokenEncoder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IRoleRepository roleRepository;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testLogin_ValidCredentials() {
        // Arrange
        String email = "aryal@don.com";
        String password = "password";

        List<UserRole> userRoles = Collections.singletonList(UserRole.USER);

        Set<Role> roles = userRoles.stream()
                .map(userRole -> {
                    Role role = new Role();
                    role.setRole(UserRole.valueOf(userRole.toString()));
                    return role;
                })
                .collect(Collectors.toSet());

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setRoles(roles);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);
        when(accessTokenEncoder.encode(any())).thenReturn("mocked-access-token");

        // Act
        AuthResponse authResponse = authService.login(loginRequest);

        // Assert
        assertNotNull(authResponse);
        assertEquals("mocked-access-token", authResponse.getAccessToken());

        // Verify
        verify(userRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, mockUser.getPassword());
        verify(accessTokenEncoder, times(1)).encode(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder, accessTokenEncoder);
    }


    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        String email = "aryal@don.com";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act and Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));

        // Verify
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository, passwordEncoder, accessTokenEncoder);
    }


    @Test
    void testCreateAdminAndFindAdminByEmail() throws AdminException {
        IUserRepository userRepository = mock(IUserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        IRoleRepository roleRepository = mock(IRoleRepository.class);

        AdminService adminService = new AdminService(userRepository, passwordEncoder, roleRepository);

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("adminPassword");

        when(userRepository.findByEmail("admin@example.com")).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(admin);

        Role adminRole = new Role();
        adminRole.setRole(UserRole.ADMIN);
        when(roleRepository.findByRole(UserRole.ADMIN)).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);

        User createdAdmin = adminService.createAdmin(admin);

        verify(userRepository, times(1)).findByEmail("admin@example.com");
        verify(userRepository, times(1)).save(any(User.class));

        verify(roleRepository, times(1)).findByRole(UserRole.ADMIN);
        verify(roleRepository, times(1)).save(any(Role.class));


        assertNotNull(createdAdmin);
        assertEquals("admin@example.com", createdAdmin.getEmail());
    }



    @Test
    void testLoginWithGoogle_UserExists() {
        // Arrange
        String googleUserEmail = "googleuser@example.com";

        User existingUser = new User();
        existingUser.setEmail(googleUserEmail);

        when(userRepository.findByEmail(googleUserEmail)).thenReturn(existingUser);
        when(accessTokenEncoder.encode(any())).thenReturn("mocked-access-token");

        // Act
        AuthResponse authResponse = authService.loginWithGoogle(googleUserEmail);

        // Assert
        assertNotNull(authResponse);
        assertEquals("mocked-access-token", authResponse.getAccessToken());

        // Verify
        verify(userRepository, times(1)).findByEmail(googleUserEmail);
        verify(accessTokenEncoder, times(1)).encode(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder, accessTokenEncoder);
    }




    @Test
    void testCreateUserFromGoogle_UserDoesNotExist() {
        // Arrange
        String googleUserEmail = "googleuser@example.com";

        when(userRepository.findByEmail(googleUserEmail)).thenReturn(null);

        // Act
        User createdUser = authService.createUserFromGoogle(googleUserEmail);

        // Assert
        assertNotNull(createdUser);
        assertEquals(googleUserEmail, createdUser.getEmail());

        // Verify
        verify(userRepository, times(1)).findByEmail(googleUserEmail);
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepository, times(1)).findByRole(UserRole.USER);
        verify(roleRepository, times(1)).save(any(Role.class));
        verifyNoMoreInteractions(userRepository, roleRepository);
    }

    @Test
    void testCreateUserFromGoogle_UserExists() {
        // Arrange
        String googleUserEmail = "googleuser@example.com";

        User existingUser = new User();
        existingUser.setEmail(googleUserEmail);

        when(userRepository.findByEmail(googleUserEmail)).thenReturn(existingUser);

        // Act
        User createdUser = authService.createUserFromGoogle(googleUserEmail);

        // Assert
        assertNotNull(createdUser);
        assertEquals(googleUserEmail, createdUser.getEmail());

        // Verify
        verify(userRepository, times(1)).findByEmail(googleUserEmail);
        verifyNoMoreInteractions(userRepository, roleRepository);
    }






}
