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

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/signup")
    public ResponseEntity<User> addUserHandler(@RequestBody User user) throws UserException {
        User savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestParam(required = false) String jwtToken) throws UserException {
        User updatedUser = userService.updateUser(user, jwtToken);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/admin/user/delete/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable("userId") Integer userId, @RequestParam(required = false) String jwtToken) throws UserException {
        User deletedUser = userService.deleteUser(userId, jwtToken);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("/admin/user/allUsers")
    public ResponseEntity<List<User> > viewAllUsers(@RequestParam(required = false) String jwtToken) throws UserException {
        List<User> userList = userService.viewAllUsers(jwtToken);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }


}
