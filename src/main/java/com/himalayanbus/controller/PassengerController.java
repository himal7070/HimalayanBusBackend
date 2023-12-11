package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IPassengerRepository;
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
    private final IPassengerRepository passengerRepository;


    public PassengerController(IPassengerService passengerService, IPassengerRepository passengerRepository) {
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addPassenger(@RequestBody User user) throws UserException {
        User newUser = passengerService.addPassenger(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }




    @DeleteMapping("/delete/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> deletePassenger(@PathVariable Long userID) throws UserException {
        User deletedUser = passengerService.deletePassenger(userID);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public List<Object[]> viewAllPassengersWithUserDetails() throws UserException {
        List<Object[]> passengerList = passengerRepository.findAllPassengersWithUserDetails();

        if (passengerList.isEmpty()) {
            throw new UserException("No passengers found!");
        }

        return passengerList;
    }


    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getTotalPassengerCount() {
        try {
            long count = passengerService.getTotalPassengerCount();
            return ResponseEntity.ok("Total : " + count);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/user/{userEmail}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Object> getUserInformationByEmail(@PathVariable String userEmail) {
        try {
            Object userInformation = passengerService.getUserInformationByEmail(userEmail);
            return ResponseEntity.ok(userInformation);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @PutMapping("/updateDetails/{passengerID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Passenger> updatePassengerDetails(
            @PathVariable Long passengerID,
            @RequestBody Passenger updatedPassenger
    ) {
        try {
            Passenger updatedPassengerInfo = passengerService.updatePassengerDetails(passengerID, updatedPassenger);
            return ResponseEntity.ok(updatedPassengerInfo);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/updatePassword/{passengerID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> updatePasswordForPassenger(
            @PathVariable Long passengerID,
            @RequestBody String newPassword
    ) {
        try {
            User updatedUser = passengerService.updatePasswordForPassenger(passengerID, newPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }






}
