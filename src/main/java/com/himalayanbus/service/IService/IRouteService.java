package com.himalayanbus.service.IService;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;

import java.util.List;

public interface IRouteService {
    Route addRoute(Route route) throws RouteException;
    List<Route> viewAllRoutes() throws RouteException;
    Route updateRoute(Route route) throws RouteException;
    Route deleteRoute(int routeID) throws RouteException;

}
