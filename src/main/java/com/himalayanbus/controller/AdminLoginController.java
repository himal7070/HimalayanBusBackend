package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.AdminLoginDTO;
import com.himalayanbus.persistence.entity.AdminLoginSession;
import com.himalayanbus.service.IService.IAdminLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.LoginException;
import javax.validation.Valid;

@RestController
@RequestMapping("/himalayanbus")
public class AdminLoginController {

    private final IAdminLoginService iAdminLoginService;


    public AdminLoginController(IAdminLoginService iAdminLoginService) {
        this.iAdminLoginService = iAdminLoginService;
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AdminLoginSession> loginAdmin(@RequestBody @Valid AdminLoginDTO loginDTO)
            throws AdminException, LoginException {
        AdminLoginSession currentAdminSession = iAdminLoginService.adminLogin(loginDTO);
        return new ResponseEntity<>(currentAdminSession, HttpStatus.ACCEPTED);
    }

    @PostMapping("/admin/logout")
    public ResponseEntity<String> logoutAdmin(@RequestParam(required = false) String key) throws LoginException {
        iAdminLoginService.adminLogout(key);
        return new ResponseEntity<>("Admin logged out.", HttpStatus.OK);
    }




}
