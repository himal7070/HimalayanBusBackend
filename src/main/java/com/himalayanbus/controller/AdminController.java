package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.service.IService.IAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/himalayanbus")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final IAdminService iAdminService;

    public AdminController(IAdminService iAdminService) {
        this.iAdminService = iAdminService;
    }


    @PostMapping("/admin/signup")
    public ResponseEntity<Admin> registerAdmin(@Valid @RequestBody Admin admin) {
        try {
            logger.info("Received a request to create admin: {}", admin);
            Admin savedAdmin = iAdminService.createAdmin(admin);
            return new ResponseEntity<>(savedAdmin, HttpStatus.CREATED);
        } catch (AdminException e) {
            logger.error("Error while creating admin: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/admin/update")
    public ResponseEntity<Admin> updateAdmin(@Valid @RequestBody Admin admin, @RequestParam(required = false) String key) {
        try {
            logger.info("Received a request to update admin: {}", admin);
            Admin updatedAdmin = iAdminService.updateAdmin(admin, key);
            return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
        } catch (AdminException e) {
            logger.error("Error while updating admin: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }




}
