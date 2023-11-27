package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IPassengerService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/himalayanbus/passenger")
public class PassengerController {

    private final IPassengerService passengerService;


    public PassengerController(IPassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addPassenger(@RequestBody User user) {
        try {
            User newUser = passengerService.addPassenger(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/update/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> updatePassenger(@PathVariable Long userID, @RequestBody User userUpdate, @RequestBody Passenger passengerUpdate) {
        try {
            User updatedUser = passengerService.updatePassenger(userID, userUpdate, passengerUpdate);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/delete/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> deletePassenger(@PathVariable Long userID) {
        try {
            User deletedUser = passengerService.deletePassenger(userID);
            return new ResponseEntity<>(deletedUser, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Passenger>> viewAllPassengers() {
        try {
            List<Passenger> passengers = passengerService.viewAllPassengers();
            return new ResponseEntity<>(passengers, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
