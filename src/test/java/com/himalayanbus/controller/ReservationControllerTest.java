package com.himalayanbus.controller;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.service.IReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private IReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @Test
    void testAddReservation() throws ReservationException {
        ReservationDTO mockDTO = new ReservationDTO();
        Long busId = 1L;
        Reservation mockReservation = new Reservation();
        when(reservationService.addReservation(mockDTO, busId)).thenReturn(mockReservation);

        ResponseEntity<Reservation> responseEntity = reservationController.addReservation(mockDTO, busId);

        assertEquals(mockReservation, responseEntity.getBody());
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        verify(reservationService, times(1)).addReservation(mockDTO, busId);
    }

    @Test
    void testGetAllReservations() throws ReservationException {
        List<Reservation> mockReservations = new ArrayList<>();
        when(reservationService.getAllReservation()).thenReturn(mockReservations);

        ResponseEntity<List<Reservation>> responseEntity = reservationController.getAllReservations();

        assertEquals(mockReservations, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(reservationService, times(1)).getAllReservation();
    }




    @Test
    void testViewReservationsForCurrentUser() throws ReservationException {
        List<Reservation> mockReservations = new ArrayList<>();
        when(reservationService.viewReservationsForCurrentUser()).thenReturn(mockReservations);

        ResponseEntity<List<Reservation>> responseEntity = reservationController.viewReservationsForCurrentUser();

        assertEquals(mockReservations, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(reservationService, times(1)).viewReservationsForCurrentUser();
    }

    @Test
    void testDeleteReservation() throws ReservationException {
        Long reservationId = 1L;
        Reservation mockReservation = new Reservation();
        when(reservationService.deleteReservation(reservationId)).thenReturn(mockReservation);

        ResponseEntity<Reservation> responseEntity = reservationController.deleteReservation(reservationId);

        assertEquals(mockReservation, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(reservationService, times(1)).deleteReservation(reservationId);
    }

    @Test
    void testCountActiveReservationsForToday_Success() throws ReservationException {
        long count = 5;
        when(reservationService.countActiveReservationsForToday()).thenReturn(count);

        ResponseEntity<Object> responseEntity = reservationController.countActiveReservationsForToday();

        assertEquals(Map.of("Total", count), responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(reservationService, times(1)).countActiveReservationsForToday();
    }

    @Test
    void testCountActiveReservationsForToday_Error() throws ReservationException {
        when(reservationService.countActiveReservationsForToday()).thenThrow(new ReservationException("Failed to count"));

        ResponseEntity<Object> responseEntity = reservationController.countActiveReservationsForToday();

        assertEquals(Map.of("error", "Failed to count"), responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(reservationService, times(1)).countActiveReservationsForToday();
    }


}