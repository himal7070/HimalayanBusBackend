package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IPassengerRepository;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.IAccessControlService;
import com.himalayanbus.security.token.impl.AccessTokenImpl;
import com.himalayanbus.service.IPassengerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PassengerControllerTest {

    @Mock
    private IPassengerService passengerService;

    @Mock
    private IPassengerRepository passengerRepository;

    @Mock
    private IAccessControlService accessControlService;

    @InjectMocks
    private PassengerController passengerController;

    @Test
    void testAddPassenger() throws UserException {
        User user = new User();
        when(passengerService.addPassenger(any(User.class))).thenReturn(user);

        ResponseEntity<User> responseEntity = passengerController.addPassenger(user);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
        verify(passengerService, times(1)).addPassenger(user);
    }

    @Test
    void testDeletePassenger() throws UserException {
        // Arrange
        Long userId = 1L;
        User deletedUser = new User(/*user details */);
        when(passengerService.deletePassenger(userId)).thenReturn(deletedUser);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<User> responseEntity = passengerController.deletePassenger(userId, "Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedUser, responseEntity.getBody());
        verify(passengerService, times(1)).deletePassenger(userId);
        verify(accessControlService, times(1)).extractAccessToken("Bearer mockToken");
    }

    @Test
    void testViewAllPassengersWithUserDetails() throws UserException {
        // Arrange
        // passengers with associated user details
        Object[] passenger1 = {"himal", "aryal", "1234567890"};
        Object[] passenger2 = {"sabina", "thapa", "9876543210"};

        List<Object[]> mockPassengerList = new ArrayList<>();
        mockPassengerList.add(passenger1);
        mockPassengerList.add(passenger2);

        when(passengerRepository.findAllPassengersWithUserDetails()).thenReturn(mockPassengerList);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        List<Object[]> result = passengerController.viewAllPassengersWithUserDetails("Bearer mockToken");

        // Assert
        assertEquals(mockPassengerList, result);
        verify(passengerRepository, times(1)).findAllPassengersWithUserDetails();
        verify(accessControlService, times(1)).extractAccessToken("Bearer mockToken");
    }

    @Test
    void testGetTotalPassengerCount() throws UserException {
        // Arrange
        long count = 5;
        when(passengerService.getTotalPassengerCount()).thenReturn(count);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<String> responseEntity = passengerController.getTotalPassengerCount("Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Total : " + count, responseEntity.getBody());
        verify(passengerService, times(1)).getTotalPassengerCount();
        verify(accessControlService, times(1)).extractAccessToken("Bearer mockToken");
    }

    @Test
    void testUpdatePassengerDetails() throws UserException {
        // Arrange
        Long passengerId = 1L;
        Passenger updatedPassenger = new Passenger(/*passenger details */);
        when(passengerService.updatePassengerDetails(passengerId, updatedPassenger)).thenReturn(updatedPassenger);

        AccessToken mockAccessToken = new AccessTokenImpl("testUser", null, Collections.singletonList("ADMIN"));
        when(accessControlService.extractAccessToken(any())).thenReturn(mockAccessToken);

        // Act
        ResponseEntity<Passenger> responseEntity = passengerController.updatePassengerDetails(passengerId, updatedPassenger, "Bearer mockToken");

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedPassenger, responseEntity.getBody());
        verify(passengerService, times(1)).updatePassengerDetails(passengerId, updatedPassenger);
        verify(accessControlService, times(1)).extractAccessToken("Bearer mockToken");
    }
}
