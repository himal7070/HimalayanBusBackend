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

    @GetMapping("/viewAll")
    public ResponseEntity<List<Bus>> getAllBusesHandler()throws BusException{
        List<Bus> allBuses = busService.viewAllBuses();
        return new ResponseEntity<>(allBuses,HttpStatus.OK);
    }







}