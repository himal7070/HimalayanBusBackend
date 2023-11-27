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
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO dto) {
        try {
            Reservation reservation = reservationService.addReservation(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @PutMapping("/update/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long reservationId,
            @RequestBody ReservationDTO dto
    ) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(reservationId, dto);
            return ResponseEntity.ok(updatedReservation);
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @GetMapping("/viewReservation/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> viewReservation(@PathVariable Long reservationId) {
        try {
            Reservation reservation = reservationService.viewReservation(reservationId);
            return ResponseEntity.status(HttpStatus.OK).body(reservation);
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/all")
    @RolesAllowed("Admin")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservation();
            return ResponseEntity.status(HttpStatus.OK).body(reservations);
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/current-user")
    @RolesAllowed("USER")
    public ResponseEntity<List<Reservation>> viewReservationsForCurrentUser() {
        try {
            List<Reservation> reservations = reservationService.viewReservationsForCurrentUser();
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{reservationId}")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long reservationId) {
        try {
            Reservation reservation = reservationService.deleteReservation(reservationId);
            return ResponseEntity.status(HttpStatus.OK).body(reservation);
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}
