package com.himalayanbus.service;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRouteService {

    @Transactional
    Route addRoute(Route route) throws RouteException;

    @Transactional
    List<Route> viewAllRoutes() throws RouteException;


    @Transactional
    Route updateRoute(Integer routeId, Route updatedRoute) throws RouteException;

    @Transactional
    Route deleteRoute(Integer routeId) throws RouteException;

    @Transactional
    Route viewRoute(Integer routeId) throws RouteException;
}
