package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.IRepository.IBusRepository;
import com.himalayanbus.persistence.IRepository.IRouteRepository;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.service.IService.IBusService;
import org.springframework.stereotype.Service;

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


    public Bus addBus(Bus bus)  throws BusException {

        try {
            Route route = bus.getRoute();
            if (route == null) {
                throw new BusException("Route information is missing.");
            }

            String routeFrom = bus.getRouteFrom();
            String routeTo = bus.getRouteTo();
            Integer distance = route.getDistance();

            //if any of the required fields are missing
            if (routeFrom == null || routeTo == null || distance == null) {
                throw new BusException("Route information is incomplete.");
            }


            route = new Route(routeFrom, routeTo, distance);

            bus.setRoute(route);

            if (route.getBusList() != null) {
                route.getBusList().add(bus);
            }

            return busRepository.save(bus);
        } catch (Exception e) {
            throw new BusException("Failed to add bus: " + e.getMessage());
        }

    }

    public List<Bus> viewAllBuses() throws BusException {
        List<Bus> busList = busRepository.findAll();
        if(busList.isEmpty()){
            throw new BusException("There is no bus available at the moment");
        }
        return busList;
    }

    public Bus updateBus(Bus bus) throws BusException {

        Optional<Bus> findBus = busRepository.findById(bus.getBusId());
        if(findBus.isEmpty()){
            throw new BusException("There is no bus available with this id: "+ bus.getBusId());
        }
        Route route = iRouteRepository.findByRouteScheduled(bus.getRouteFrom(),bus.getRouteTo());
        if (route != null) {
            throw new BusException("A bus on this route has already been scheduled.");
        } else {
            Route scheduledRoute = new Route(bus.getRouteFrom(), bus.getRouteTo(), bus.getRoute().getDistance());
            iRouteRepository.save(scheduledRoute);
            bus.setRoute(scheduledRoute);
            return busRepository.save(bus);
        }
    }
    public Bus deleteBus(Integer busId) throws BusException{

        Optional<Bus> bus = busRepository.findById(busId);

        if(bus.isPresent()){
            Bus existingBus = bus.get();

            //checks if the current date is before the journey date and checks if seats are reserved
            if(LocalDate.now().isBefore(existingBus.getJourneyDate()) && !Objects.equals(existingBus.getAvailableSeats(), existingBus.getTotalSeats())){
                throw new BusException("Cannot delete scheduled bus.");
            }
            busRepository.delete(existingBus);
            return existingBus;

        } else throw  new BusException("There is no such bus with this busId: "+busId);

    }

    public List<Bus> viewBusType(String busType) throws BusException {
        List<Bus> busListType = busRepository.findByBusType(busType);
        if(busListType.isEmpty()){

            throw new BusException("There are no buses with bus type: "+ busType);
        }
        return busListType;
    }


}
