package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        Role userRole = new Role();
        userRole.setRole(UserRole.valueOf("USER"));
        when(roleRepository.findByRole(UserRole.valueOf("USER"))).thenReturn(userRole);

        when(roleRepository.save(userRole)).thenReturn(userRole);
        when(userRepository.save(user)).thenReturn(user);

        try {
            User addedUser = userService.addUser(user);
            assertNotNull(addedUser);
            assertEquals("test@example.com", addedUser.getEmail());
            assertTrue(addedUser.getRoles().contains(userRole));
        } catch (UserException e) {
            fail("UserException should not be thrown");
        }
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setUserID(1);
        existingUser.setFirstName("Himal");

        User updatedUser = new User();
        updatedUser.setUserID(1);
        updatedUser.setFirstName("Himal Aryal");

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        try {
            User result = userService.updateUser(1, updatedUser);
            assertNotNull(result);
            assertEquals("Himal Aryal", result.getFirstName());
        } catch (UserException e) {
            fail("UserException should not be thrown");
        }
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setUserID(1);
        user.setEmail("test@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        try {
            User deletedUser = userService.deleteUser(1);
            assertNotNull(deletedUser);
            assertEquals(1, deletedUser.getUserID());
        } catch (UserException e) {
            fail("UserException should not be thrown");
        }
    }

    @Test
    void testViewAllUsers() {
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setUserID(1);
        User user2 = new User();
        user2.setUserID(2);
        userList.add(user1);
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        try {
            List<User> result = userService.viewAllUsers();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(user1));
            assertTrue(result.contains(user2));
        } catch (UserException e) {
            fail("UserException should not be thrown");
        }
    }
}
