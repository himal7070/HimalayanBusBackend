package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/himalayanbus/admin")
public class AdminController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> createAdmin(@RequestBody User admin) {
        try {
            User createdAdmin = adminService.createAdmin(admin);
            return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
        } catch (AdminException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/update/{adminID}")
    public ResponseEntity<User> updateAdmin(@RequestBody User admin, @PathVariable Long adminID) {
        try {
            User updatedAdmin = adminService.updateAdmin(admin, adminID);
            return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
        } catch (AdminException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


}
