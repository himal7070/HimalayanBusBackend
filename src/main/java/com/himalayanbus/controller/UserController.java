package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IUserService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            User newUser = userService.addUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/update/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> updateUser(@PathVariable Integer userID, @RequestBody User updatedUser) {
        try {
            User updated = userService.updateUser(userID, updatedUser);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> deleteUser(@PathVariable Integer userID) {
        try {
            User deletedUser = userService.deleteUser(userID);
            return new ResponseEntity<>(deletedUser, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<User>> viewAllUsers() {
        try {
            List<User> userList = userService.viewAllUsers();
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
