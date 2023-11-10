package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService implements IRouteService {

    private final IRouteRepository iRouteRepository;

    public RouteService(IRouteRepository iRouteRepository)
    {
        this.iRouteRepository = iRouteRepository;
    }



    @Override
    @Transactional
    public Route addRoute(Route route) throws RouteException {
        Route existingRoute = iRouteRepository.findByRouteScheduled();
        if (existingRoute != null) {
            throw new RouteException("Route: " + existingRoute.getRouteFrom() + " to " + existingRoute.getRouteTo() + " is already present!");
        }

        return iRouteRepository.save(route);
    }



    @Override
    public List<Route> viewAllRoutes() throws RouteException {
        List<Route> routes = iRouteRepository.findAll();
        if (routes.isEmpty()) {
            throw new RouteException("There are no routes available at the moment");
        }
        return routes;
    }



    @Override
    @Transactional
    public Route updateRoute(Route route) throws RouteException {
        Optional<Route> existingRoute = iRouteRepository.findById(route.getRouteID());
        if (existingRoute.isPresent()) {
            Route presentRoute = existingRoute.get();
            if (!presentRoute.getBusList().isEmpty()) {
                throw new RouteException("Route cannot be updated; a bus in this route has already been scheduled");
            }
            return iRouteRepository.save(route);
        } else {
            throw new RouteException("Route not found with this routeId: " + route.getRouteID());
        }
    }



    @Override
    @Transactional
    public Route deleteRoute(int routeID) throws RouteException {
        Optional<Route> route = iRouteRepository.findById(routeID);
        if (route.isPresent()) {
            Route existingRoute = route.get();
            iRouteRepository.delete(existingRoute);
            return existingRoute;
        } else {
            throw new RouteException("Route not found with this id: " + routeID);
        }
    }


}
