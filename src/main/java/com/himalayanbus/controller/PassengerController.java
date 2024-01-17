package com.himalayanbus.controller;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IPassengerRepository;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.IAccessControlService;
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
    private final IAccessControlService accessControlService;


    public PassengerController(IPassengerService passengerService, IPassengerRepository passengerRepository, IAccessControlService accessControlService) {
        this.passengerService = passengerService;
        this.passengerRepository = passengerRepository;
        this.accessControlService = accessControlService;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addPassenger(@RequestBody User user) throws UserException {

        User newUser = passengerService.addPassenger(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);

    }




    @DeleteMapping("/delete/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<User> deletePassenger(@PathVariable Long userID,  @RequestHeader("Authorization") String authorizationHeader) throws UserException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, userID.toString());

        User deletedUser = passengerService.deletePassenger(userID);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public List<Object[]> viewAllPassengersWithUserDetails( @RequestHeader("Authorization") String authorizationHeader) throws UserException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        List<Object[]> passengerList = passengerRepository.findAllPassengersWithUserDetails();

        if (passengerList.isEmpty()) {
            throw new UserException("No passengers found!");
        }

        return passengerList;
    }


    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getTotalPassengerCount( @RequestHeader("Authorization") String authorizationHeader) {
        try {

            AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
            accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

            long count = passengerService.getTotalPassengerCount();
            return ResponseEntity.ok("Total : " + count);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @PutMapping("/updateDetails/{userID}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Passenger> updatePassengerDetails(
            @PathVariable Long userID,
            @RequestBody Passenger updatedPassenger,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
            accessControlService.checkUserAccess(accessToken, userID.toString());

            Passenger updatedPassengerInfo = passengerService.updatePassengerDetails(userID, updatedPassenger);
            return ResponseEntity.ok(updatedPassengerInfo);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }





}
