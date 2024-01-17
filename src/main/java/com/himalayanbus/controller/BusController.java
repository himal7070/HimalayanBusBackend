package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.security.token.IAccessControlService;
import com.himalayanbus.service.IBusService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/himalayanbus/bus")
@CrossOrigin(origins = "http://localhost:5173")
public class BusController {

    private final IBusService busService;
    private final IAccessControlService accessControlService;
    public BusController(IBusService busService, IAccessControlService accessControlService) {
        this.busService = busService;
        this.accessControlService = accessControlService;
    }

    @PostMapping("/add")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> addBus(@RequestBody Bus bus,  @RequestHeader("Authorization") String authorizationHeader) throws BusException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        Bus createdBus = busService.addBus(bus);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewAllBuses( @RequestHeader("Authorization") String authorizationHeader) throws BusException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        List<Bus> busList = busService.viewAllBus();
        return ResponseEntity.status(HttpStatus.OK).body(busList);
    }

    @PutMapping("/update/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> updateBus(@PathVariable Long busId, @RequestBody Bus newBusDetails,  @RequestHeader("Authorization") String authorizationHeader) throws BusException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        Bus updatedBus = busService.updateBus(busId, newBusDetails);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBus);
    }


    @DeleteMapping("/delete/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> deleteBus(@PathVariable Long busId,  @RequestHeader("Authorization") String authorizationHeader) {
        try {

            AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
            accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

            busService.deleteBus(busId);
            return ResponseEntity.status(HttpStatus.OK).body("Bus deleted successfully");
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete bus: " + e.getMessage());
        }
    }



    @GetMapping("/type/{busType}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewBusByType(@PathVariable String busType,  @RequestHeader("Authorization") String authorizationHeader) throws BusException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        List<Bus> busList = busService.viewBusByType(busType);
        return ResponseEntity.status(HttpStatus.OK).body(busList);
    }


    @GetMapping("viewBus/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> viewBus(@PathVariable Long busId,  @RequestHeader("Authorization") String authorizationHeader) throws BusException {

        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        Bus bus = busService.viewBus(busId);
        return ResponseEntity.status(HttpStatus.OK).body(bus);
    }


    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getTotalBusCount( @RequestHeader("Authorization") String authorizationHeader) {
        try {

            AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
            accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

            long count = busService.countAllBuses();
            return ResponseEntity.ok("Total : " + count);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }



    @GetMapping("/search")
    @RolesAllowed("USER")
    public ResponseEntity<List<Bus>> searchBusByRoute(
            @RequestParam(required = false) String routeFrom,
            @RequestParam(required = false) String routeTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws BusException {
        AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
        accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

        if (routeFrom == null && routeTo == null) {
            throw new IllegalArgumentException("At least one route field should be provided");
        }

        List<Bus> busList = busService.searchBusByRoute(
                Optional.ofNullable(routeFrom),
                Optional.ofNullable(routeTo),
                journeyDate
        );

        return ResponseEntity.status(HttpStatus.OK).body(busList);

    }



    @PutMapping("/delayDeparture/{busId}/{delayMinutes}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> delayBusDeparture(
            @PathVariable Long busId,
            @PathVariable Long delayMinutes,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {

            AccessToken accessToken = accessControlService.extractAccessToken(authorizationHeader);
            accessControlService.checkUserAccess(accessToken, accessToken.getSubject());

            Duration delayDuration = Duration.ofMinutes(delayMinutes);

            String updatedBusMessage = busService.delayBusDeparture(busId, delayDuration);

            return ResponseEntity.ok(updatedBusMessage);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




}