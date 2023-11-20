package com.himalayanbus.controller;


import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IRouteService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus/route")
public class RouteController {

    private final IRouteService routeService;

    public RouteController(IRouteService routeService) {
        this.routeService = routeService;
    }


    @PostMapping("/add")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> addRoute(@RequestBody Route newRoute) {
        try {
            Route addedRoute = routeService.addRoute(newRoute);
            return new ResponseEntity<>(addedRoute, HttpStatus.CREATED);
        } catch (RouteException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Route>> viewAllRoutes() {
        try {
            List<Route> routeList = routeService.viewAllRoutes();
            return new ResponseEntity<>(routeList, HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{routeId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> updateRoute(@PathVariable Integer routeId, @RequestBody Route updatedRoute) {
        try {
            Route updated = routeService.updateRoute(routeId, updatedRoute);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{routeID}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> deleteRoute(@PathVariable int routeID) {
        try {
            Route deletedRoute = routeService.deleteRoute(routeID);
            return new ResponseEntity<>(deletedRoute, HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/view/{routeId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> viewRoute(@PathVariable Integer routeId) {
        try {
            Route route = routeService.viewRoute(routeId);
            return new ResponseEntity<>(route, HttpStatus.OK);
        } catch (RouteException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }





}
