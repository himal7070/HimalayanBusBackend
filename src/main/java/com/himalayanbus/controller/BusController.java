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


}