package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.IRepository.IBusRepository;
import com.himalayanbus.persistence.IRepository.IRouteRepository;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IService.IBusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BusService implements IBusService {

    private final IBusRepository busRepository;

    private final IRouteRepository iRouteRepository;

    public BusService(IBusRepository busRepository, IRouteRepository iRouteRepository) {
        this.busRepository = busRepository;
        this.iRouteRepository = iRouteRepository;
    }


    @Override
    @Transactional
    public Bus addBus(Bus bus) throws BusException {
        Route route = validateAndCreateRoute(bus);
        bus.setRoute(route);
        return busRepository.save(bus);
    }

    @Override
    public List<Bus> viewAllBuses() throws BusException {
        List<Bus> busList = busRepository.findAll();
        if (busList.isEmpty()) {
            throw new BusException("There are no buses available at the moment");
        }
        return busList;
    }

    @Override
    @Transactional
    public Bus updateBus(Bus bus) throws BusException {
        Optional<Bus> findBus = busRepository.findById(bus.getBusId());
        if (findBus.isEmpty()) {
            throw new BusException("No bus found with id: " + bus.getBusId());
        }

        Route route = iRouteRepository.findByRouteScheduled();
        if (route != null) {
            throw new BusException("A bus on this route has already been scheduled.");
        } else {
            Route scheduledRoute = new Route(bus.getRouteFrom(), bus.getRouteTo(), bus.getRoute().getDistance());
            iRouteRepository.save(scheduledRoute);
            bus.setRoute(scheduledRoute);
            return busRepository.save(bus);
        }
    }

    @Override
    @Transactional
    public Bus deleteBus(Integer busId) throws BusException {
        Optional<Bus> bus = busRepository.findById(busId);

        if (bus.isPresent()) {
            Bus existingBus = bus.get();
            if (canDeleteBus(existingBus)) {
                busRepository.delete(existingBus);
                return existingBus;
            } else {
                throw new BusException("Cannot delete scheduled bus.");
            }
        } else {
            throw new BusException("No bus found with id: " + busId);
        }
    }

    @Override
    public List<Bus> viewBusType(String busType) throws BusException {
        List<Bus> busListType = busRepository.findByBusType(busType);
        if (busListType.isEmpty()) {
            throw new BusException("No buses found with bus type: " + busType);
        }
        return busListType;
    }



    //-------------------------------------- validation to a separate method --------------------------------------



    private Route validateAndCreateRoute(Bus bus) throws BusException {
        Route route = bus.getRoute();
        if (route == null) {
            throw new BusException("Route information is missing.");
        }

        String routeFrom = bus.getRouteFrom();
        String routeTo = bus.getRouteTo();
        Integer distance = route.getDistance();

        if (routeFrom == null || routeTo == null || distance == null) {
            throw new BusException("Route information is incomplete.");
        }

        Route existingRoute = iRouteRepository.findByRouteScheduled();
        if (existingRoute != null) {
            throw new BusException("A bus on this route has already been scheduled.");
        }

        route = new Route(routeFrom, routeTo, distance);
        return route;
    }

    private boolean canDeleteBus(Bus bus) {
        return LocalDate.now().isBefore(bus.getJourneyDate()) && !Objects.equals(bus.getAvailableSeats(), bus.getTotalSeats());
    }


}
