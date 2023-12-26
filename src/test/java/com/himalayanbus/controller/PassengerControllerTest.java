package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IPassengerRepository;
import com.himalayanbus.service.IPassengerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
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
        Long userId = 1L;
        User deletedUser = new User(/*user details */);
        when(passengerService.deletePassenger(userId)).thenReturn(deletedUser);

        ResponseEntity<User> responseEntity = passengerController.deletePassenger(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(deletedUser, responseEntity.getBody());
        verify(passengerService, times(1)).deletePassenger(userId);
    }

    @Test
    void testViewAllPassengersWithUserDetails() throws UserException {
        // passengers with associated user details
        Object[] passenger1 = {"himal", "aryal", "1234567890"};
        Object[] passenger2 = {"sabina", "thapa", "9876543210"};

        List<Object[]> mockPassengerList = new ArrayList<>();
        mockPassengerList.add(passenger1);
        mockPassengerList.add(passenger2);

        when(passengerRepository.findAllPassengersWithUserDetails()).thenReturn(mockPassengerList);

        List<Object[]> result = passengerController.viewAllPassengersWithUserDetails();

        assertEquals(mockPassengerList, result);
        verify(passengerRepository, times(1)).findAllPassengersWithUserDetails();
    }

    @Test
    void testGetTotalPassengerCount() throws UserException {
        long count = 5;
        when(passengerService.getTotalPassengerCount()).thenReturn(count);

        ResponseEntity<String> responseEntity = passengerController.getTotalPassengerCount();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Total : " + count, responseEntity.getBody());
        verify(passengerService, times(1)).getTotalPassengerCount();
    }

    @Test
    void testUpdatePassengerDetails() throws UserException {
        Long passengerId = 1L;
        Passenger updatedPassenger = new Passenger(/*passenger details */);
        when(passengerService.updatePassengerDetails(passengerId, updatedPassenger)).thenReturn(updatedPassenger);

        ResponseEntity<Passenger> responseEntity = passengerController.updatePassengerDetails(passengerId, updatedPassenger);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedPassenger, responseEntity.getBody());
        verify(passengerService, times(1)).updatePassengerDetails(passengerId, updatedPassenger);
    }
}
