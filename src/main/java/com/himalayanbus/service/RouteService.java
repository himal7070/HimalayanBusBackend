package com.himalayanbus.service;

import com.himalayanbus.exception.RouteException;
import com.himalayanbus.persistence.IRepository.IRouteRepository;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IService.IRouteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService implements IRouteService {

    private final IRouteRepository iRouteRepository;

    public RouteService(IRouteRepository iRouteRepository) {
        this.iRouteRepository = iRouteRepository;
    }

    public Route addRoute(Route route) throws RouteException {

        Route newRoute = iRouteRepository.findByRouteScheduled(route.getRouteFrom(), route.getRouteTo());

        if(newRoute != null) throw new RouteException("Route :"+ newRoute.getRouteFrom() +" to "+ newRoute.getRouteTo()+ " is already present !");

        List<Bus> buses = new ArrayList<>();

        route.setBusList(buses);
        return iRouteRepository.save(route);

    }

    public List<Route> viewAllRoute() throws RouteException {

        List<Route> routes=iRouteRepository.findAll();
        if(routes.isEmpty())

            throw new RouteException("There is no route available at the moment");

        else

            return routes;
    }


    public Route updateRoute(Route route) throws RouteException{

        Optional<Route> existedRoute = iRouteRepository.findById(route.getRouteID());
        if(existedRoute.isPresent()) {

            Route presentRoute = existedRoute.get();
            List<Bus> busList = presentRoute.getBusList();

            if(!busList.isEmpty()) throw new RouteException("Route cannot be updated; a bus in this route already has been scheduled");

            return iRouteRepository.save(route);
        }
        else
            throw new RouteException("Route doesn't exist of  this routeId : "+ route.getRouteID());

    }

    public Route deleteRoute(int routeID) throws RouteException {

        Optional<Route> route=iRouteRepository.findById(routeID);

        if(route.isPresent()) {
            Route existingRoute=route.get();
            iRouteRepository.delete(existingRoute);
            return existingRoute;
        }
        else
            throw new RouteException("Route not found with this id: "+ routeID);

    }


}
