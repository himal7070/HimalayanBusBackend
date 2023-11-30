package com.himalayanbus.controller;


import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.service.IReservationService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus/reservation")
public class ReservationController {

    private final IReservationService reservationService;



    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;

    }

    @PostMapping("/add")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO dto) throws ReservationException {
        Reservation reservation = reservationService.addReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }


    @PutMapping("/update/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationDTO dto
    ) throws ReservationException {
        Reservation updatedReservation = reservationService.updateReservation(reservationId, dto);
        return ResponseEntity.ok(updatedReservation);
    }


    @GetMapping("/viewReservation/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> viewReservation(@PathVariable Long reservationId) throws ReservationException {
        Reservation reservation = reservationService.viewReservation(reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }


    @GetMapping("/all")
    @RolesAllowed("Admin")
    public ResponseEntity<List<Reservation>> getAllReservations() throws ReservationException {
        List<Reservation> reservations = reservationService.getAllReservation();
        return ResponseEntity.status(HttpStatus.OK).body(reservations);
    }

    @GetMapping("/current-user")
    @RolesAllowed("USER")
    public ResponseEntity<List<Reservation>> viewReservationsForCurrentUser() throws ReservationException {
        List<Reservation> reservations = reservationService.viewReservationsForCurrentUser();
        return ResponseEntity.status(HttpStatus.OK).body(reservations);
    }

    @DeleteMapping("/delete/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long reservationId) throws ReservationException {
        Reservation reservation = reservationService.deleteReservation(reservationId);
        return ResponseEntity.status(HttpStatus.OK).body(reservation);
    }


}
