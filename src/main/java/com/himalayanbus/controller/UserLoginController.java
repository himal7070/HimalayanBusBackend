package com.himalayanbus.controller;


import com.himalayanbus.persistence.entity.UserLoginDTO;
import com.himalayanbus.persistence.entity.UserLoginSession;
import com.himalayanbus.service.IService.IUserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/himalayanbus")
public class UserLoginController {

    private final IUserLoginService iUserLoginService;


    public UserLoginController(IUserLoginService iUserLoginService)
    {
        this.iUserLoginService = iUserLoginService;
    }

    @PostMapping("/user/login")
    public ResponseEntity<UserLoginSession> logInUser(@RequestBody UserLoginDTO loginDTO) throws LoginException {
        UserLoginSession userLoginSession = iUserLoginService.userLogin(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userLoginSession);
    }

    @PostMapping("/user/logout")
    public String logoutUser(@RequestParam(required = false) String key) throws LoginException {
        return iUserLoginService.userLogout(key);
    }



}
