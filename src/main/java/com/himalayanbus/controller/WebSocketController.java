package com.himalayanbus.controller;


import com.himalayanbus.dtos.WebSocketMessage;
import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.service.IReservationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@CrossOrigin(origins = {"http://localhost:5173"})
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final IReservationService reservationService;


    public WebSocketController(SimpMessagingTemplate messagingTemplate, IReservationService reservationService) {
        this.messagingTemplate = messagingTemplate;
        this.reservationService = reservationService;

    }

    @MessageMapping("/delayBusDeparture")
    public void sendDelayNotification(@Payload WebSocketMessage webSocketMessage) throws ReservationException {
        Long busId = webSocketMessage.getBusId();
        String delayMessage = webSocketMessage.getMessage();

        List<Reservation> reservations = reservationService.getAllReservationsByBusId(busId);

        for (Reservation reservation : reservations) {
            Passenger passenger = reservation.getPassenger();
            if (passenger != null) {
                Long userId = passenger.getUser().getUserID();
                String notify = "/user/" + userId + "/queue/notifications";

                messagingTemplate.convertAndSend(notify, delayMessage);
            }
        }
    }

}
