package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IService.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus")
public class UserController {

    private final IUserService iUserService;

    public UserController(IUserService iUserService) {
        this.iUserService = iUserService;
    }


    @PostMapping("/user/signup")
    public ResponseEntity<User> addUserHandler(@RequestBody User user) throws UserException {
        User savedUser = iUserService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestParam(required = false) String sessionKey) throws UserException {
        User updatedUser = iUserService.updateUser(user, sessionKey);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") Integer userId, @RequestParam(required = false) String sessionKey) throws UserException {
        User deletedUser = iUserService.deleteUser(userId, sessionKey);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("/user/allUsers")
    public ResponseEntity<List<User>> viewAllUsers(@RequestParam(required = false) String sessionKey) throws UserException {
        List<User> userList = iUserService.viewAllUsers(sessionKey);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }



}
