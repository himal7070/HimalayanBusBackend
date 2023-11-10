package com.himalayanbus.controller;


import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.service.IService.IReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus")
public class ReservationController {

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO dto, @RequestHeader("Authorization") String jwtToken) throws ReservationException {
        Reservation reservation = reservationService.addReservation(dto, jwtToken);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @PutMapping("/reservations/{reservationID}")
    public ResponseEntity<Reservation> updateReservation(@RequestBody ReservationDTO dto, @RequestHeader("Authorization") String jwtToken, @PathVariable Integer reservationID) throws ReservationException {
        Reservation reservation = reservationService.updateReservation(reservationID, dto, jwtToken);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @DeleteMapping("/reservations/{reservationID}")
    public ResponseEntity<Reservation> deleteReservation(@RequestHeader("Authorization") String jwtToken, @PathVariable Integer reservationID) throws ReservationException {
        Reservation reservation = reservationService.deleteReservation(reservationID, jwtToken);
        return new ResponseEntity<>(reservation, HttpStatus.ACCEPTED);
    }

    @GetMapping("/reservations/{reservationID}")
    public ResponseEntity<Reservation> viewReservationById(@PathVariable Integer reservationID, @RequestHeader("Authorization") String jwtToken) throws ReservationException {
        Reservation reservation = reservationService.viewReservationByRID(reservationID, jwtToken);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }

    @GetMapping("/reservations/all")
    public ResponseEntity<List<Reservation>> viewAllReservations(@RequestHeader("Authorization") String jwtToken) throws ReservationException {
        List<Reservation> reservations = reservationService.getAllReservation(jwtToken);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/reservations/user/{userID}")
    public ResponseEntity<List<Reservation>> viewReservationByUserId(@PathVariable Integer userID, @RequestHeader("Authorization") String jwtToken) throws ReservationException {
        List<Reservation> reservations = reservationService.viewReservationByUserId(userID, jwtToken);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

}
