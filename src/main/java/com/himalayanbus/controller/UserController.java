package com.himalayanbus.controller;


import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IUserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/himalayanbus/user")
public class UserController {

    private final IUserService userService;

    public UserController( IUserService userService) {
        this.userService = userService;

    }

    @GetMapping("/{userEmail}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Object> getUserInformationByEmail(@PathVariable String userEmail) throws UserException {
        Object userInformation = userService.getUserInformationByEmail(userEmail);
        return ResponseEntity.ok(userInformation);
    }


    @PutMapping("/updatePassword")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> updatePasswordForUser(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) throws UserException {
        if (email == null || email.isEmpty() || oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            throw new UserException("Email, old password, or new password cannot be empty.");
        }
        User updatedUser = userService.updatePasswordByEmail(email, oldPassword, newPassword);
        return ResponseEntity.ok(updatedUser);
    }





}
