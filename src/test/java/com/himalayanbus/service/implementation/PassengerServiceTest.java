package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IPassengerRepository;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PassengerServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IPassengerRepository passengerRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPassenger_ValidUser() throws UserException {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        Passenger passenger = new Passenger();
        user.setPassenger(passenger);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByRole(any())).thenReturn(new Role());
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user).thenReturn(user);
        when(passengerRepository.save(any())).thenReturn(passenger);

        // Act
        User resultUser = passengerService.addPassenger(user);

        // Assert
        assertNotNull(resultUser);
        assertEquals("test@example.com", resultUser.getEmail());
        assertEquals(1, resultUser.getRoles().size());
        assertNotNull(resultUser.getPassenger());
        assertEquals("hashedPassword", resultUser.getPassword());

        // Verify
        verify(userRepository, times(2)).save(user);
        verify(passengerRepository, times(1)).save(any());
    }


    @Test
    void testUpdatePassengerDetails_ValidPassenger() throws UserException {
        // Arrange
        Long userId = 1L;

        // Mocking user and passenger
        User existingUser = new User();
        existingUser.setUserID(userId);

        Passenger existingPassenger = new Passenger();
        existingPassenger.setFirstName("OldFirstName");
        existingPassenger.setLastName("OldLastName");
        existingPassenger.setPhoneNumber("OldPhoneNumber");
        existingUser.setPassenger(existingPassenger);

        // Mocking repository behavior
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passengerRepository.save(any())).thenReturn(existingPassenger);

        // Updated passenger details
        Passenger updatedPassenger = new Passenger();
        updatedPassenger.setFirstName("Himal");
        updatedPassenger.setLastName("Aryal");
        updatedPassenger.setPhoneNumber("1234567890");

        // Act
        Passenger resultPassenger = passengerService.updatePassengerDetails(userId, updatedPassenger);

        // Assert
        assertNotNull(resultPassenger);
        assertEquals("Himal", resultPassenger.getFirstName());
        assertEquals("Aryal", resultPassenger.getLastName());
        assertEquals("1234567890", resultPassenger.getPhoneNumber());

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(passengerRepository, times(1)).save(existingPassenger);


    }






    @Test
    void testDeletePassenger_ValidUser() throws UserException {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserID(userId);
        Passenger passenger = new Passenger();
        user.setPassenger(passenger);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User resultUser = passengerService.deletePassenger(userId);

        // Assert
        assertNotNull(resultUser);
        assertEquals(userId, resultUser.getUserID());
        assertNull(resultUser.getPassenger());

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(passengerRepository, times(1)).delete(passenger);
        verify(userRepository, times(1)).delete(user);
    }




    @Test
    void testAddPassenger_UserAlreadyExists_ThrowUserException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        Passenger passenger = new Passenger();
        user.setPassenger(passenger);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.addPassenger(user));
        verify(userRepository, times(0)).save(user);
        verify(passengerRepository, times(0)).save(any());
    }

    @Test
    void testAddPassenger_PassengerDetailsNull_ThrowUserException() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.addPassenger(user));
        verify(userRepository, times(0)).save(user);
        verify(passengerRepository, times(0)).save(any());
    }






    @Test
    void testDeletePassenger_UserNotFound_ThrowUserException() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.deletePassenger(userId));
        verify(passengerRepository, times(0)).delete(any());
        verify(userRepository, times(0)).delete(any());
    }

    @Test
    void testViewAllPassengers_NoPassengersExist_ThrowUserException() {
        // Arrange
        when(passengerRepository.findAllPassengersWithUserDetails()).thenReturn(List.of());

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.viewAllPassengersWithUserDetails());
        verify(passengerRepository, times(1)).findAllPassengersWithUserDetails();
    }




    @Test
    void testViewAllPassengers_PassengersExist() {
        // Arrange
        when(passengerRepository.findAllPassengersWithUserDetails()).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.viewAllPassengersWithUserDetails());

        // Verify
        verify(passengerRepository, times(1)).findAllPassengersWithUserDetails();
    }



    @Test
    void testGetTotalPassengerCount_NoPassengersAvailable_ThrowUserException() {
        // Arrange
        when(passengerRepository.count()).thenReturn(0L);

        // Act & Assert
        assertThrows(UserException.class, () -> passengerService.getTotalPassengerCount());
    }



    @Test
    void testAddPassenger_WhenUserRoleDoesNotExist_CreatesNewUserRole() throws UserException {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        Passenger passenger = new Passenger();
        user.setPassenger(passenger);

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByRole(any())).thenReturn(null);
        when(roleRepository.save(any())).thenReturn(new Role());
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userRepository.save(user)).thenReturn(user).thenReturn(user);
        when(passengerRepository.save(any())).thenReturn(passenger);

        // Act
        User resultUser = passengerService.addPassenger(user);

        // Assert
        assertNotNull(resultUser);
        assertEquals("hashedPassword", resultUser.getPassword());
        assertEquals(1, resultUser.getRoles().size());

        // Verify
        verify(userRepository, times(2)).save(user);
        verify(passengerRepository, times(1)).save(any());
    }











}
