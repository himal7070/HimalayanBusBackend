package com.himalayanbus.controller;


import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.IAccessControlService;
import com.himalayanbus.service.IUserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/himalayanbus/user")
public class UserController {

    private final IUserService userService;
    private final IAccessControlService accessControlService;
    public UserController(IUserService userService, IAccessControlService accessControlService) {
        this.userService = userService;
        this.accessControlService = accessControlService;
    }


    @GetMapping("/{userEmail}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Object> getUserInformationByEmail(
            @PathVariable String userEmail,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws UserException {
        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, userEmail);

        Object userInformation = userService.getUserInformationByEmail(userEmail);
        return ResponseEntity.ok(userInformation);
    }



    @PutMapping("/updatePassword")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> updatePasswordForUser(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestHeader("Authorization") String authorizationHeader

    ) throws UserException {
        if (email == null || email.isEmpty() || oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new UserException("Email, old password, or new password cannot be empty.");
        }
        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, email);

        User updatedUser = userService.updatePasswordByEmail(email, oldPassword, newPassword);
        return ResponseEntity.ok(updatedUser);
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetUserPassword(@RequestParam String email) {
        try {
            userService.initiatePasswordReset(email);
            return ResponseEntity.ok("Password reset initiated successfully. Please check your email for further instructions.");
        } catch (UserException e) {
            return ResponseEntity.badRequest().body("Failed to initiate password reset: " + e.getMessage());
        }
    }

    @PutMapping("/completeReset")
    public ResponseEntity<String> completePasswordReset(
            @RequestParam String email,
            @RequestParam String resetToken,
            @RequestParam String newPassword
    ) {
        try {
            userService.completePasswordReset(email, resetToken, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (UserException e) {
            return ResponseEntity.badRequest().body("Failed to reset password: " + e.getMessage());
        }
    }



}
