package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.service.IBusService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus/bus")
public class BusController {

    private final IBusService busService;

    public BusController(IBusService busService) {
        this.busService = busService;
    }

    @PostMapping("/add")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> addBus(@RequestBody Bus bus) {
        try {
            Bus createdBus = busService.addBus(bus);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBus);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewAllBuses() {
        try {
            List<Bus> busList = busService.viewAllBus();
            return ResponseEntity.status(HttpStatus.OK).body(busList);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/update/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> updateBus(@PathVariable Long busId, @RequestBody Bus newBusDetails) {
        try {
            Bus updatedBus = busService.updateBus(busId, newBusDetails);
            return ResponseEntity.status(HttpStatus.OK).body(updatedBus);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    @DeleteMapping("/delete/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> deleteBus(@PathVariable Long busId) {
        try {
            busService.deleteBus(busId);
            return ResponseEntity.status(HttpStatus.OK).body("Bus deleted successfully");
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus not found");
        }

    }


    @GetMapping("/type/{busType}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Bus>> viewBusByType(@PathVariable String busType) {
        try {
            List<Bus> busList = busService.viewBusByType(busType);
            return ResponseEntity.status(HttpStatus.OK).body(busList);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("viewBus/{busId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Bus> viewBus(@PathVariable Long busId) {
        try {
            Bus bus = busService.viewBus(busId);
            return ResponseEntity.status(HttpStatus.OK).body(bus);
        } catch (BusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}