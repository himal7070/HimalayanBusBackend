package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetUserInformationByEmail_UserExists_ReturnsUserInfo() {
        String userEmail = "aryal@gmail.com";
        User user = new User();
        user.setEmail(userEmail);

        Passenger passenger = new Passenger();
        passenger.setFirstName("Himal");
        passenger.setLastName("Aryal");
        passenger.setPhoneNumber("1234567890");

        user.setPassenger(passenger);

        when(userRepository.findByEmail(userEmail)).thenReturn(user);

        try {
            Object userInfo = userService.getUserInformationByEmail(userEmail);
            assertNotNull(userInfo);
            assertInstanceOf(Map.class, userInfo);

            Map<?, ?> userInformation = (Map<?, ?>) userInfo;
            assertEquals(userEmail, userInformation.get("email"));
            assertEquals("Himal", userInformation.get("firstName"));
            assertEquals("Aryal", userInformation.get("lastName"));
            assertEquals("1234567890", userInformation.get("phoneNumber"));
            assertFalse(userInformation.containsKey("imageProfileUrl"));
        } catch (UserException e) {
            fail("Exception should not be thrown for existing user");
        }

    }


    @Test
    void testGetUserInformationByEmail_UserNotFound_ThrowsUserException() {
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(null);

        assertThrows(UserException.class, () ->
                userService.getUserInformationByEmail(nonExistentEmail)
        );

    }


    @Test
    void testUpdatePasswordByEmail_ValidInput_NoDatabaseChange() {
        String userEmail = "aryal@gmail.com";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        User existingUser = new User();
        existingUser.setEmail(userEmail);
        existingUser.setPassword("encodedPassword");

        when(userRepository.findByEmail(userEmail)).thenReturn(existingUser);
        when(passwordEncoder.matches(oldPassword, existingUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("newEncodedPassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        try {
            User updatedUser = userService.updatePasswordByEmail(userEmail, oldPassword, newPassword);
            assertNotNull(updatedUser);
            assertEquals(existingUser, updatedUser);
            verify(userRepository, times(1)).save(existingUser);
        } catch (UserException e) {
            fail("Exception should not be thrown for valid input");
        }
    }


}
