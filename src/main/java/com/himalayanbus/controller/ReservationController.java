package com.himalayanbus.controller;


import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.service.IReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus/reservations")
public class ReservationController {

    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/add")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO dto, @RequestHeader("Authorization") String jwtToken) {
        try {
            Reservation reservation = reservationService.addReservation(dto, jwtToken);
            return new ResponseEntity<>(reservation, HttpStatus.CREATED);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{rid}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Integer rid, @RequestBody ReservationDTO dto, @RequestHeader("Authorization") String jwtToken) {
        try {
            Reservation reservation = reservationService.updateReservation(rid, dto, jwtToken);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/viewReservation/{rid}")
    public ResponseEntity<Reservation> viewReservation(@PathVariable Integer rid, @RequestHeader("Authorization") String jwtToken) {
        try {
            Reservation reservation = reservationService.viewReservation(rid, jwtToken);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllReservations(@RequestHeader("Authorization") String jwtToken) {
        try {
            List<Reservation> reservations = reservationService.getAllReservation(jwtToken);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<List<Reservation>> viewReservationsByUserId(@PathVariable Integer uid, @RequestHeader("Authorization") String jwtToken) {
        try {
            List<Reservation> reservations = reservationService.viewReservationByUerId(uid, jwtToken);
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{rid}")
    public ResponseEntity<String> deleteReservation(@PathVariable Integer rid, @RequestHeader("Authorization") String jwtToken) {
        try {
            Reservation deletedReservation = reservationService.deleteReservation(rid, jwtToken);
            return new ResponseEntity<>("Reservation with ID " + deletedReservation.getReservationID() + " deleted.", HttpStatus.OK);
        } catch (ReservationException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
