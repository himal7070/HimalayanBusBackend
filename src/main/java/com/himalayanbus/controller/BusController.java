package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.service.IBusService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/himalayanbus/bus")
@CrossOrigin(origins = "http://localhost:5173")
public class BusController {

    private final IBusService busService;

    public BusController(IBusService busService) {
        this.busService = busService;
    }

    @PostMapping("/add")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> addBus(@RequestBody Bus bus) throws BusException {
        Bus createdBus = busService.addBus(bus);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewAllBuses() throws BusException {
        List<Bus> busList = busService.viewAllBus();
        return ResponseEntity.status(HttpStatus.OK).body(busList);
    }

    @PutMapping("/update/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> updateBus(@PathVariable Long busId, @RequestBody Bus newBusDetails) throws BusException {
        Bus updatedBus = busService.updateBus(busId, newBusDetails);
        return ResponseEntity.status(HttpStatus.OK).body(updatedBus);
    }


    @DeleteMapping("/delete/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> deleteBus(@PathVariable Long busId) throws BusException {
        busService.deleteBus(busId);
        return ResponseEntity.status(HttpStatus.OK).body("Bus deleted successfully");
    }


    @GetMapping("/type/{busType}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewBusByType(@PathVariable String busType) throws BusException {
        List<Bus> busList = busService.viewBusByType(busType);
        return ResponseEntity.status(HttpStatus.OK).body(busList);
    }


    @GetMapping("viewBus/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> viewBus(@PathVariable Long busId) throws BusException {
        Bus bus = busService.viewBus(busId);
        return ResponseEntity.status(HttpStatus.OK).body(bus);
    }


    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getTotalBusCount() {
        try {
            long count = busService.countAllBuses();
            return ResponseEntity.ok("Total : " + count);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }


    @GetMapping("/search/{routeFrom}/{routeTo}")
    @RolesAllowed("USER")
    public ResponseEntity<List<Bus>> searchBusByRoute(
            @PathVariable String routeFrom,
            @PathVariable String routeTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate
    ) throws BusException {

            List<Bus> busList = busService.searchBusByRoute(routeFrom, routeTo, journeyDate);
            return ResponseEntity.status(HttpStatus.OK).body(busList);

    }

//
//    @PatchMapping("/delayDeparture/{busId}")
//    @RolesAllowed("ADMIN")
//    public ResponseEntity<Bus> delayBusDeparture(
//            @PathVariable Long busId,
//            @RequestParam(name = "delayMinutes") Long delayMinutes
//    ) throws BusException {
//        Duration delayDuration = Duration.ofMinutes(delayMinutes);
//
//        // Call the service to delay the bus departure
//        Bus updatedBus = busService.delayBusDeparture(busId, delayDuration);
//
//        // Return the updated bus details in the response
//        return ResponseEntity.ok(updatedBus);
//    }

    @PutMapping("/delayDeparture/{busId}/{delayMinutes}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> delayBusDeparture(
            @PathVariable Long busId,
            @PathVariable Long delayMinutes
    ) {
        try {
            Duration delayDuration = Duration.ofMinutes(delayMinutes);

            String updatedBusMessage = busService.delayBusDeparture(busId, delayDuration);

            return ResponseEntity.ok(updatedBusMessage);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




}