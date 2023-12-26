package com.himalayanbus.controller;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IAdminService;
import jakarta.annotation.security.RolesAllowed;
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
    public ResponseEntity<User> createAdmin(@RequestBody User admin) throws AdminException {
        User createdAdmin = adminService.createAdmin(admin);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }


    @PutMapping("/update/{adminID}")
    public ResponseEntity<User> updateAdmin(@RequestBody User admin, @PathVariable Long adminID) throws AdminException {
        User updatedAdmin = adminService.updateAdmin(admin, adminID);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
    }

    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getCountOfAdmins() {
        try {
            long adminCount = adminService.countAdmins();
            return ResponseEntity.ok("Total : " + adminCount);
        } catch (AdminException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



}
