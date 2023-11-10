package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.service.IService.IAdminService;
import com.himalayanbus.service.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/himalayanbus")
public class AdminController {

    private final IAdminService adminService;
    private final JwtTokenUtil jwtTokenUtil;

    public AdminController(IAdminService adminService, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin, @RequestParam String jwtToken) throws AdminException {
        validateAdmin(jwtToken);
        Admin createdAdmin = adminService.createAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Admin> updateAdmin(@RequestBody Admin admin, @RequestParam String jwtToken) throws AdminException {
        validateAdmin(jwtToken);
        Admin updatedAdmin = adminService.updateAdmin(admin, jwtToken);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
    }

    private void validateAdmin(String jwtToken) throws AdminException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);
        if (!"admin".equals(claims.get("role"))) {
            throw new AdminException("Unauthorized: Admin role required.");
        }
    }


}
