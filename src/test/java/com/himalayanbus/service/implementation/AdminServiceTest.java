package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAdmin_Success() throws AdminException {
        // Arrange
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        Role mockAdminRole = new Role();
        mockAdminRole.setRole(UserRole.ADMIN);

        when(userRepository.findByEmail(any())).thenReturn(null);
        when(roleRepository.findByRole(UserRole.ADMIN)).thenReturn(null);
        when(roleRepository.save(any())).thenReturn(mockAdminRole);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userRepository.save(any())).thenReturn(admin);

        // Act
        User createdAdmin = adminService.createAdmin(admin);

        // Assert
        assertNotNull(createdAdmin);
        assertEquals("hashedPassword", createdAdmin.getPassword());
        assertEquals(UserRole.ADMIN, createdAdmin.getRoles().iterator().next().getRole());

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(any());
        verify(roleRepository, times(1)).findByRole(UserRole.ADMIN);
        verify(roleRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
        verifyNoMoreInteractions(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void testCreateAdmin_EmailAlreadyExists() {
        // Arrange
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("password");

        when(userRepository.findByEmail(any())).thenReturn(admin);

        // Act and Assert
        assertThrows(AdminException.class, () -> adminService.createAdmin(admin));

        // Verify interactions
        verify(userRepository, times(1)).findByEmail(any());
        verifyNoMoreInteractions(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void testUpdateAdminPassword_AdminNotFound() {
        Long adminId = 1L;

        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(AdminException.class, () -> adminService.updateAdmin(new User(), adminId));
        verify(userRepository, times(1)).findById(adminId);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }



    @Test
    void testCountAdmins_AdminsExist() {
        long expectedCount = 3;

        when(userRepository.countAdmins()).thenReturn(expectedCount);

        try {
            long count = adminService.countAdmins();
            assertEquals(expectedCount, count);
            verify(userRepository, times(1)).countAdmins();
        } catch (AdminException e) {
            fail("AdminException should not be thrown");
        }
    }

    @Test
    void testCountAdmins_NoAdminsAvailable() {
        when(userRepository.countAdmins()).thenReturn(0L);

        assertThrows(AdminException.class, () -> adminService.countAdmins());
        verify(userRepository, times(1)).countAdmins();
    }







}
