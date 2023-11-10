package com.himalayanbus.service;

import com.himalayanbus.persistence.IRepository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private UserService userService;

    @Test
    void testAddUser() {
        // Arrange
        Mockito.when(userRepository.existsByEmail(any())).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(new User());

        // Act
        User user = new User();
        assertDoesNotThrow(() -> userService.addUser(user));

        // Assert and Verify
        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(any());
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        // Arrange
        Mockito.when(userRepository.save(any(User.class))).thenReturn(new User());

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("sub")).thenReturn(1);
        Mockito.when(claims.get("role")).thenReturn("admin");
        Mockito.when(jwtTokenUtil.validateJwtToken(any())).thenReturn(claims);

        // Act
        User user = new User();
        user.setUserID(1);
        assertDoesNotThrow(() -> userService.updateUser(user, "mockJwtToken"));

        // Assert and Verify
        Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Mockito.when(userRepository.findById(eq(1))).thenReturn(Optional.of(new User()));
        Mockito.doNothing().when(userRepository).delete(any(User.class));


        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("sub")).thenReturn(1);
        Mockito.when(claims.get("role")).thenReturn("admin");
        Mockito.when(jwtTokenUtil.validateJwtToken(any())).thenReturn(claims);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser(1, "mockJwtToken"));

        // Assert and Verify
        Mockito.verify(userRepository, Mockito.times(1)).findById(eq(1));
        Mockito.verify(userRepository, Mockito.times(1)).delete(any(User.class));
    }

    @Test
    void testViewAllUsers() {
        // Arrange
        Mockito.when(userRepository.findAll()).thenReturn(Collections.singletonList(new User()));

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.get("role")).thenReturn("admin");
        Mockito.when(jwtTokenUtil.validateJwtToken(any())).thenReturn(claims);

        // Act
        List<User> userList = assertDoesNotThrow(() -> userService.viewAllUsers("mockJwtToken"));

        // Assert and Verify
        Mockito.verify(userRepository, Mockito.times(1)).findAll();

        // Verify the result
        assertNotNull(userList);
        assertFalse(userList.isEmpty());
    }
}
