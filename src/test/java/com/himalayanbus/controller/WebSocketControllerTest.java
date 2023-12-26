package com.himalayanbus.controller;

import com.himalayanbus.dtos.WebSocketMessage;
import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.service.IReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private IReservationService reservationService;

    @InjectMocks
    private WebSocketController webSocketController;

    @Test
    void testSendDelayNotification() throws ReservationException {
        WebSocketMessage mockWebSocketMessage = new WebSocketMessage();
        mockWebSocketMessage.setBusId(1L);
        mockWebSocketMessage.setMessage("Bus delayed by 30 minutes");

        Reservation mockReservation = new Reservation();
        Passenger mockPassenger = new Passenger();
        mockPassenger.setPassengerId(1L);
        mockReservation.setPassenger(mockPassenger);

        List<Reservation> mockReservations = new ArrayList<>();
        mockReservations.add(mockReservation);

        when(reservationService.getAllReservationsByBusId(mockWebSocketMessage.getBusId())).thenReturn(mockReservations);

        webSocketController.sendDelayNotification(mockWebSocketMessage);

        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyString());

    }

}
