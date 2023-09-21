package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.model.Bus;
import com.himalayanbus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus")
public class BusController {

    @Autowired
    private BusService busService;


    @PostMapping("/add")
    public ResponseEntity<Bus> addBusHandler(@RequestBody Bus bus)throws BusException {

        Bus addedBus = busService.addBus(bus);
        return new ResponseEntity<>(addedBus, HttpStatus.CREATED);
    }

    @GetMapping("/view")
    public ResponseEntity<List<Bus>> getAllBusesHandler()throws BusException{
        List<Bus> allBuses = busService.viewAllBuses();
        return new ResponseEntity<>(allBuses,HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Bus> updateBusHandler(@RequestBody Bus bus) throws BusException{
        Bus newBus = busService.updateBus(bus);
        return new ResponseEntity<>(newBus,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{busId}")
    public ResponseEntity<Bus> deleteBusByBusIdHandler(@PathVariable("busId") Integer busId) throws BusException{
        Bus deletedBus = busService.deleteBus(busId);
        return new ResponseEntity<>(deletedBus,HttpStatus.OK);
    }








}