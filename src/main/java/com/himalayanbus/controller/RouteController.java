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
    public ResponseEntity<Route> addRoute(@RequestBody Route newRoute) throws RouteException {
        Route addedRoute = routeService.addRoute(newRoute);
        return new ResponseEntity<>(addedRoute, HttpStatus.CREATED);
    }


    @GetMapping("/viewAll")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<Route>> viewAllRoutes() throws RouteException {
        List<Route> routeList = routeService.viewAllRoutes();
        return new ResponseEntity<>(routeList, HttpStatus.OK);
    }


    @PutMapping("/update")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> updateRoute(@RequestBody Route updatedRoute) throws RouteException {
        Route updated = routeService.updateRoute(updatedRoute);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/delete/{routeID}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> deleteRoute(@PathVariable Long routeID) throws RouteException {
        Route deletedRoute = routeService.deleteRoute(routeID);
        return new ResponseEntity<>(deletedRoute, HttpStatus.OK);
    }


    @GetMapping("/view/{routeId}")
    @RolesAllowed("ADMIN")
    public ResponseEntity<Route> viewRoute(@PathVariable Long routeId) throws RouteException {
        Route route = routeService.viewRoute(routeId);
        return new ResponseEntity<>(route, HttpStatus.OK);
    }

    @GetMapping("/count")
    @RolesAllowed("ADMIN")
    public ResponseEntity<String> getTotalRouteCount() {
        try {
            long count = routeService.getTotalRouteCount();
            return ResponseEntity.ok("Total : " + count);
        } catch (RouteException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }





}
