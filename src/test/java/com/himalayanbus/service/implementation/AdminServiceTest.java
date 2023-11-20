package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IAdminRepository;
import com.himalayanbus.persistence.repository.IRoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class AdminServiceTest {

    @Mock
    private IAdminRepository adminRepository;

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAdmin() {
        Admin newAdmin = new Admin();
        newAdmin.setEmail("test1@example.com");
        newAdmin.setPassword("password");
        newAdmin.setUserName("himal");

        Mockito.when(adminRepository.findByEmail(Mockito.anyString())).thenReturn(null);

        Mockito.when(roleRepository.findByRole(Mockito.any())).thenReturn(null);

        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setRole(UserRole.ADMIN);
        Mockito.when(roleRepository.save(Mockito.any())).thenReturn(savedRole);

        Admin savedAdmin = new Admin();
        savedAdmin.setAdminID(1);
        savedAdmin.setEmail("test1@example.com");
        savedAdmin.setPassword("password");
        savedAdmin.setUserName("himal");
        Mockito.when(adminRepository.save(Mockito.any())).thenReturn(savedAdmin);

        try {
            Admin createdAdmin = adminService.createAdmin(newAdmin);
            Assertions.assertEquals("test1@example.com", createdAdmin.getEmail());
            Assertions.assertEquals("password", createdAdmin.getPassword());
            Assertions.assertEquals("himal", createdAdmin.getUserName());
        } catch (AdminException e) {
            Assertions.fail("AdminException should not be thrown");
        }
    }

    @Test
    void testUpdateAdmin() {
        Admin adminToUpdate = new Admin();
        adminToUpdate.setAdminID(1);
        adminToUpdate.setEmail("test1@example.com");
        adminToUpdate.setPassword("password");
        adminToUpdate.setUserName("himal");

        Mockito.when(adminRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(adminToUpdate));

        Admin updatedAdmin = new Admin();
        updatedAdmin.setAdminID(1);
        updatedAdmin.setEmail("test1@example.com");
        updatedAdmin.setPassword("newpassword");
        updatedAdmin.setUserName("himal dark coder");
        Mockito.when(adminRepository.save(Mockito.any())).thenReturn(updatedAdmin);

        try {
            Admin resultAdmin = adminService.updateAdmin(updatedAdmin, 1);
            Assertions.assertEquals("test1@example.com", resultAdmin.getEmail());
            Assertions.assertEquals("newpassword", resultAdmin.getPassword());
            Assertions.assertEquals("himal dark coder", resultAdmin.getUserName());
        } catch (AdminException e) {
            Assertions.fail("AdminException should not be thrown");
        }
    }
}
