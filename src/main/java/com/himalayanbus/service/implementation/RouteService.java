package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.service.IRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService implements IRouteService {

    private final IRouteRepository routeRepository;

    public RouteService(IRouteRepository iRouteRepository)
    {
        this.routeRepository = iRouteRepository;
    }



    @Override
    @Transactional
    public Route addRoute(Route newRoute) throws RouteException {
        Route existingRoute = routeRepository.findByRouteFromAndRouteTo(newRoute.getRouteFrom(), newRoute.getRouteTo());
        if (existingRoute != null) {
            throw new RouteException("Route from " + newRoute.getRouteFrom() + " to " + newRoute.getRouteTo() + " already exists");
        }

        newRoute.setBusList(new ArrayList<>());
        return routeRepository.save(newRoute);
    }



    @Override
    @Transactional
    public List<Route> viewAllRoutes() throws RouteException {
        List<Route> routeList = routeRepository.findAll();
        if (routeList.isEmpty()) {
            throw new RouteException("Route list is empty");
        } else {
            return routeList;
        }
    }



    @Override
    @Transactional
    public Route updateRoute(Integer routeId, Route updatedRoute) throws RouteException {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (optionalRoute.isPresent()) {
            Route existingRoute = optionalRoute.get();
            if (!existingRoute.getBusList().isEmpty()) {
                throw new RouteException("Cannot update Route! Buses are already scheduled for this route");
            }
            if (updatedRoute.getDistance() != null) existingRoute.setDistance(updatedRoute.getDistance());
            if (updatedRoute.getRouteFrom() != null) existingRoute.setRouteFrom(updatedRoute.getRouteFrom());
            if (updatedRoute.getRouteTo() != null) existingRoute.setRouteTo(updatedRoute.getRouteTo());

            return routeRepository.save(existingRoute);
        } else {
            throw new RouteException("No route exists with ID: " + routeId);
        }
    }




    @Override
    @Transactional
    public Route deleteRoute(Integer routeId) throws RouteException {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (optionalRoute.isPresent()) {
            Route routeToDelete = optionalRoute.get();
            if (!routeToDelete.getBusList().isEmpty()) {
                throw new RouteException("Cannot delete Route! Buses are already scheduled for this route");
            }
            routeRepository.delete(routeToDelete);
            return routeToDelete;
        } else {
            throw new RouteException("No route found with ID: " + routeId);
        }
    }


    @Override
    @Transactional
    public Route viewRoute(Integer routeId) throws RouteException {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        if (optionalRoute.isPresent()) {
            return optionalRoute.get();
        } else {
            throw new RouteException("No route found with ID: " + routeId);
        }
    }



}
