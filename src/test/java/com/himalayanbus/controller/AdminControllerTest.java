package com.himalayanbus.controller;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IAdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private IAdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @Test
    void testCreateAdmin() throws AdminException {
        User mockAdmin = new User();
        when(adminService.createAdmin(mockAdmin)).thenReturn(mockAdmin);

        ResponseEntity<User> responseEntity = adminController.createAdmin(mockAdmin);

        assertEquals(mockAdmin, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(adminService, times(1)).createAdmin(mockAdmin);
    }

    @Test
    void testUpdateAdmin() throws AdminException {
        Long adminID = 1L;
        User mockAdmin = new User();
        when(adminService.updateAdmin(mockAdmin, adminID)).thenReturn(mockAdmin);

        ResponseEntity<User> responseEntity = adminController.updateAdmin(mockAdmin, adminID);

        assertEquals(mockAdmin, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(adminService, times(1)).updateAdmin(mockAdmin, adminID);
    }

    @Test
    void testGetCountOfAdmins_Success() throws AdminException {
        long adminCount = 5;
        when(adminService.countAdmins()).thenReturn(adminCount);

        ResponseEntity<String> responseEntity = adminController.getCountOfAdmins();

        assertEquals("Total : " + adminCount, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(adminService, times(1)).countAdmins();
    }

    @Test
    void testGetCountOfAdmins_Error() throws AdminException {
        when(adminService.countAdmins()).thenThrow(new AdminException("Failed to get admin count"));

        ResponseEntity<String> responseEntity = adminController.getCountOfAdmins();

        assertEquals("Failed to get admin count", responseEntity.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(adminService, times(1)).countAdmins();
    }
}
