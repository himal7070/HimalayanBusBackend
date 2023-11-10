package com.himalayanbus.controller;


import com.himalayanbus.persistence.entity.UserLoginDTO;
import com.himalayanbus.service.IService.IUserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/himalayanbus")
public class UserLoginController {

    private final IUserLoginService userLoginService;

    public UserLoginController(IUserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        try {
            String jwtToken = userLoginService.userLogin(userLoginDTO);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (LoginException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<String> userLogout() {
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }

}
