package com.himalayanbus.controller;


import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IService.IRouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/himalayanbus")
public class RouteController {

    private final IRouteService iRouteService;

    public RouteController(IRouteService iRouteService) {
        this.iRouteService = iRouteService;
    }

    @PostMapping("/route/add")
    public ResponseEntity<Route> addRoute(@RequestBody Route route) throws RouteException {

        Route newRoute= iRouteService.addRoute(route);

        return new ResponseEntity<>(newRoute, HttpStatus.ACCEPTED);
    }

    @GetMapping("/route/view")
    public ResponseEntity<List<Route>> getAllRouteHandler() throws RouteException{

        List<Route> route= iRouteService.viewAllRoutes();

        return new ResponseEntity<>(route,HttpStatus.OK);
    }

    @PutMapping("/route/update")
    public ResponseEntity<Route> updateRoute(@RequestBody Route route) throws RouteException{

        Route newRoute= iRouteService.updateRoute(route);

        return new ResponseEntity<>(newRoute, HttpStatus.OK);
    }

    @DeleteMapping("/route/delete/{routeID}")
    public ResponseEntity<Route> DeleteRoute(@PathVariable Integer routeID) throws RouteException{

        Route route = iRouteService.deleteRoute(routeID);

        return new ResponseEntity<>(route, HttpStatus.OK);
    }







}
