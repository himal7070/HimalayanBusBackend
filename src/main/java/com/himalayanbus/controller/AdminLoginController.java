package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.AdminLoginDTO;
import com.himalayanbus.service.IService.IAdminLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/himalayanbus")
public class AdminLoginController {

    private final IAdminLoginService adminLoginService;

    public AdminLoginController(IAdminLoginService adminLoginService) {
        this.adminLoginService = adminLoginService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<String> adminLogin(@RequestBody AdminLoginDTO loginDTO) {
        try {
            String jwtToken = adminLoginService.adminLogin(loginDTO);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (LoginException | AdminException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<String> adminLogout(@RequestHeader("Authorization") String jwtToken) {
        adminLoginService.adminLogout(jwtToken);
        return new ResponseEntity<>("Admin logged out successfully", HttpStatus.OK);
    }



}
