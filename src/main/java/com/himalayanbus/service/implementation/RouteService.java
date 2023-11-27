package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.service.IRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RouteService implements IRouteService {

    private final IRouteRepository routeRepository;

    public RouteService(IRouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public Route addRoute(Route newRoute) throws RouteException {
        validateRouteNotExists(newRoute.getRouteFrom(), newRoute.getRouteTo());
        newRoute.setBusList(List.of());

        return routeRepository.save(newRoute);
    }

    private void validateRouteNotExists(String routeFrom, String routeTo) throws RouteException {
        Route existingRoute = routeRepository.findByRouteFromAndRouteTo(routeFrom, routeTo);
        if (existingRoute != null) {
            throw new RouteException("Route from " + routeFrom + " to " + routeTo + " already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> viewAllRoutes() throws RouteException {
        List<Route> routeList = routeRepository.findAll();
        if (routeList.isEmpty()) {
            throw new RouteException("Route list is empty");
        }
        return routeList;
    }

    @Override
    @Transactional
    public Route updateRoute(Route updatedRoute) throws RouteException {
        Long routeId = updatedRoute.getRouteID();

        Route existingRoute = getExistingRoute(routeId);
        validateBusesNotScheduled(existingRoute);

        updateRouteDetails(existingRoute, updatedRoute);

        return routeRepository.save(existingRoute);
    }







    private Route getExistingRoute(Long routeId) throws RouteException {
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteException("No route exists with ID: " + routeId));
    }

    private void validateBusesNotScheduled(Route existingRoute) throws RouteException {
        if (!existingRoute.getBusList().isEmpty()) {
            throw new RouteException("Cannot update Route! Buses are already scheduled for this route");
        }
    }

    private void updateRouteDetails(Route existingRoute, Route updatedRoute) {
        if (updatedRoute.getDistance() != null) {
            existingRoute.setDistance(updatedRoute.getDistance());
        }
        if (updatedRoute.getRouteFrom() != null) {
            existingRoute.setRouteFrom(updatedRoute.getRouteFrom());
        }
        if (updatedRoute.getRouteTo() != null) {
            existingRoute.setRouteTo(updatedRoute.getRouteTo());
        }
    }


    @Override
    @Transactional
    public Route deleteRoute(Long routeId) throws RouteException {
        Route routeToDelete = getExistingRoute(routeId);
        validateBusesNotScheduled(routeToDelete);

        routeRepository.delete(routeToDelete);
        return routeToDelete;
    }

    @Override
    @Transactional(readOnly = true)
    public Route viewRoute(Long routeId) throws RouteException {
        return getExistingRoute(routeId);
    }


}
