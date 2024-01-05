package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;



    @Test
    void testGetUserInformationByEmail() throws UserException {
        String userEmail = "aryal@himal.nl";
        User mockUser = createMockUser();

        when(userService.getUserInformationByEmail(userEmail)).thenReturn(mockUser);

        ResponseEntity<Object> responseEntity = userController.getUserInformationByEmail(userEmail);

        assertEquals(mockUser, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).getUserInformationByEmail(userEmail);
    }

    @Test
    void testUpdatePasswordForUser() throws UserException {
        String email = "aryal@himal.nl";
        String oldPassword = "oldPass";
        String newPassword = "newPass";
        User mockUpdatedUser = createMockUser();

        when(userService.updatePasswordByEmail(email, oldPassword, newPassword)).thenReturn(mockUpdatedUser);

        ResponseEntity<User> responseEntity = userController.updatePasswordForUser(email, oldPassword, newPassword);

        assertEquals(mockUpdatedUser, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).updatePasswordByEmail(email, oldPassword, newPassword);
    }


    public static User createMockUser() {
        User user = new User();
        user.setUserID(1L);
        user.setEmail("aryal@himal.nl");
        user.setPassword("password");
        user.setImageProfileUrl("https://example.com/profile.jpg");


        Role role = new Role();
        role.setId(1L);
        role.setRole(UserRole.valueOf("USER"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        //passenger entity associated with the user
        Passenger passenger = new Passenger();
        passenger.setPassengerId(1L);
        passenger.setFirstName("Himal");
        passenger.setLastName("aryal");
        passenger.setPhoneNumber("1234567890");
        passenger.setUser(user);

        user.setPassenger(passenger);

        return user;
    }



    @Test
    void testResetUserPassword_Success() throws UserException {
        String email = "aryal@himal.nl";
        String successMessage = "Password reset initiated successfully. Please check your email for further instructions.";

        ResponseEntity<String> expectedResponse = ResponseEntity.ok(successMessage);

        doNothing().when(userService).initiatePasswordReset(email);

        ResponseEntity<String> responseEntity = userController.resetUserPassword(email);

        assertEquals(expectedResponse, responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).initiatePasswordReset(email);
    }

    @Test
    void testResetUserPassword_Failure() throws UserException {
        String email = "aryal@himal.nl";
        String errorMessage = "Failed to initiate password reset: User not found for the provided email during password reset.";

        doThrow(new UserException("User not found for the provided email during password reset.")).when(userService).initiatePasswordReset(email);

        ResponseEntity<String> responseEntity = userController.resetUserPassword(email);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }



    @Test
    void testCompletePasswordReset_Success() throws UserException {
        String email = "aryal@himal.nl";
        String resetToken = "validToken";
        String newPassword = "newPass";
        String successMessage = "Password reset successfully.";

        ResponseEntity<String> expectedResponse = ResponseEntity.ok(successMessage);

        doNothing().when(userService).completePasswordReset(email, resetToken, newPassword);

        ResponseEntity<String> responseEntity = userController.completePasswordReset(email, resetToken, newPassword);

        assertEquals(expectedResponse, responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(userService, times(1)).completePasswordReset(email, resetToken, newPassword);
    }

    @Test
    void testCompletePasswordReset_Failure() throws UserException {
        String email = "aryal@himal.nl";
        String resetToken = "invalidToken";
        String newPassword = "newPass";
        String errorMessage = "Failed to reset password: Invalid or expired reset token.";

        doThrow(new UserException("Invalid or expired reset token.")).when(userService).completePasswordReset(email, resetToken, newPassword);

        ResponseEntity<String> responseEntity = userController.completePasswordReset(email, resetToken, newPassword);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }





}
