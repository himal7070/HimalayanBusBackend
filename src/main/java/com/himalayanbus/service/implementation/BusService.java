package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.service.IBusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BusService implements IBusService {

    private final IBusRepository busRepository;
    private final IRouteRepository routeRepository;

    private static final String BUS_ID_NOT_FOUND_MESSAGE = "Bus with id ";
    private static final String NOT_EXIST_MESSAGE = " does not exist";


    public BusService(IBusRepository busRepository, IRouteRepository routeRepository) {
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    @Transactional
    public Bus addBus(Bus bus) throws BusException {
        Route route = routeRepository.findByRouteFromAndRouteTo(bus.getRouteFrom(), bus.getRouteTo());

        if (route == null) {
            throw new BusException("Route details not found for adding the bus");
        }

        if (route.getBusList().contains(bus)) {
            throw new BusException("The bus already exists on this route");
        }

        route.getBusList().add(bus);
        bus.setRoute(route);

        return busRepository.save(bus);
    }

    @Override
    @Transactional
    public Bus updateBus(Long busId, Bus newBusDetails) throws BusException {
        Optional<Bus> optionalBus = busRepository.findById(busId);

        if (optionalBus.isPresent()) {
            Bus existingBus = optionalBus.get();

            if (!Objects.equals(existingBus.getAvailableSeats(), existingBus.getTotalSeats())) {
                throw new BusException("Cannot update a bus that already has scheduled seats");
            }



            Route route = routeRepository.findByRouteFromAndRouteTo(existingBus.getRouteFrom(), existingBus.getRouteTo());

            if (newBusDetails.getRouteFrom() != null && newBusDetails.getRouteTo() != null) {
                route = routeRepository.findByRouteFromAndRouteTo(newBusDetails.getRouteFrom(), newBusDetails.getRouteTo());

                if (route == null) {
                    throw new BusException("Invalid route details for the bus update");
                }
            }

            updateBusDetails(existingBus, newBusDetails);

            Bus updatedBus = busRepository.save(existingBus);
            route.getBusList().add(updatedBus);
            route.getBusList().remove(existingBus);

            return updatedBus;
        }

        throw new BusException(BUS_ID_NOT_FOUND_MESSAGE + busId + NOT_EXIST_MESSAGE);
    }


    void updateBusDetails(Bus existingBus, Bus newBusDetails) {
        if (newBusDetails.getArrivalTime() != null) {
            existingBus.setArrivalTime(newBusDetails.getArrivalTime());
        }
        if (newBusDetails.getAvailableSeats() != null) {
            existingBus.setAvailableSeats(newBusDetails.getAvailableSeats());
        }
        if (newBusDetails.getBusName() != null) {
            existingBus.setBusName(newBusDetails.getBusName());
        }
        if (newBusDetails.getBusType() != null) {
            existingBus.setBusType(newBusDetails.getBusType());
        }
        if (newBusDetails.getDepartureTime() != null) {
            existingBus.setDepartureTime(newBusDetails.getDepartureTime());
        }
        if (newBusDetails.getDriverName() != null) {
            existingBus.setDriverName(newBusDetails.getDriverName());
        }
        if (newBusDetails.getRouteFrom() != null) {
            existingBus.setRouteFrom(newBusDetails.getRouteFrom());
        }
        if (newBusDetails.getRouteTo() != null) {
            existingBus.setRouteTo(newBusDetails.getRouteTo());
        }
        if (newBusDetails.getTotalSeats() != null) {
            existingBus.setTotalSeats(newBusDetails.getTotalSeats());
        }
    }

    @Override
    @Transactional
    public Bus deleteBus(Long busId) throws BusException {
        Optional<Bus> optionalBus = busRepository.findById(busId);

        if (optionalBus.isPresent()) {
            Bus busToDelete = optionalBus.get();

            if (!Objects.equals(busToDelete.getAvailableSeats(), busToDelete.getTotalSeats())) {
                throw new BusException("Cannot delete a bus that already has scheduled seats");
            }

            busRepository.delete(busToDelete);
            return busToDelete;
        }

        throw new BusException(BUS_ID_NOT_FOUND_MESSAGE + busId + NOT_EXIST_MESSAGE);
    }


    @Override
    @Transactional
    public Bus viewBus(Long busId) throws BusException {
        Optional<Bus> optionalBus = busRepository.findById(busId);

        if (optionalBus.isPresent()) {
            return optionalBus.get();
        }

        throw new BusException(BUS_ID_NOT_FOUND_MESSAGE + busId + NOT_EXIST_MESSAGE);
    }

    @Override
    @Transactional
    public List<Bus> viewBusByType(String busType) throws BusException {
        List<Bus> busList = busRepository.findByBusType(busType);

        if (busList.isEmpty()) {
            throw new BusException("No buses found for the given type");
        }

        return busList;
    }

    @Override
    @Transactional
    public List<Bus> viewAllBus() throws BusException {
        List<Bus> busList = busRepository.findAll();

        if (busList.isEmpty()) {
            throw new BusException("No buses found in the system");
        }

        return busList;
    }


}
