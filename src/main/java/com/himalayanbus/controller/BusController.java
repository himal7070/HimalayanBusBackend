package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.service.IService.IBusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus")
public class BusController {

    private final IBusService busService;
    public BusController(IBusService busService) {
        this.busService = busService;
    }

    @PostMapping("/bus/add")
    public ResponseEntity<Bus> addBusHandler(@RequestBody Bus bus)throws BusException {

        Bus addedBus = busService.addBus(bus);
        return new ResponseEntity<>(addedBus, HttpStatus.CREATED);
    }

    @GetMapping("/bus/view all")
    public ResponseEntity<List<Bus>> getAllBusesHandler()throws BusException{

        List<Bus> allBuses = busService.viewAllBuses();
        return new ResponseEntity<>(allBuses,HttpStatus.OK);

    }

    @PutMapping("/bus/update")
    public ResponseEntity<Bus> updateBusHandler(@RequestBody Bus bus) throws BusException{

        Bus newBus = busService.updateBus(bus);
        return new ResponseEntity<>(newBus,HttpStatus.OK);

    }

    @DeleteMapping("/bus/delete/{busId}")
    public ResponseEntity<Bus> deleteBusByBusIdHandler(@PathVariable("busId") Integer busId) throws BusException{

        Bus deletedBus = busService.deleteBus(busId);
        return new ResponseEntity<>(deletedBus,HttpStatus.OK);

    }

    @GetMapping("/bus/bus-type/{busType}")
    public ResponseEntity<List<Bus>> getBusesByBusTypeHandler(@PathVariable("busType") String busType) throws BusException{

        List<Bus> busList = busService.viewBusType(busType);
        return new ResponseEntity<>(busList,HttpStatus.OK);

    }



}