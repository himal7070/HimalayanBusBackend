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
import java.util.Map;

@RestController
@RequestMapping("/himalayanbus/reservation")
public class ReservationController {

    private final IReservationService reservationService;



    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;

    }

    @PostMapping("/add")
    @RolesAllowed("USER")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO dto,
                                                      @RequestParam Long busId) throws ReservationException {
        Reservation reservation = reservationService.addReservation(dto, busId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }




    @GetMapping("/all")
    @RolesAllowed("ADMIN")
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


    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Object> countActiveReservationsForToday() {
        try {
            long count = reservationService.countActiveReservationsForToday();
            return ResponseEntity.ok(Map.of("Total", count));
        } catch (ReservationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }



}
