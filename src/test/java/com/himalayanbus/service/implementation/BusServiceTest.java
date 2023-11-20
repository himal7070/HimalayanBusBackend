package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IRouteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusServiceTest {

    @Mock
    private IBusRepository busRepository;

    @Mock
    private IRouteRepository routeRepository;

    @InjectMocks
    private BusService busService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testAddBus_WhenRouteDoesNotExist() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(null);

        assertThrows(BusException.class, () -> busService.addBus(bus));

        verify(busRepository, never()).save(any());
    }

    @Test
    void testAddBus_WhenBusAlreadyExistsOnRoute() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");

        Route route = new Route();
        route.getBusList().add(bus);

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(route);

        assertThrows(BusException.class, () -> busService.addBus(bus));

        verify(busRepository, never()).save(any());
    }

    @Test
    void testAddBus_SuccessfulBusAddition() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");

        Route route = new Route();

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(route);
        when(busRepository.save(bus)).thenReturn(bus); // Assuming successful save

        try {
            busService.addBus(bus);
        } catch (BusException e) {
            //exception or fail test if not expected -aryal
        }

        verify(busRepository, times(1)).save(bus);
    }



    @Test
    void testUpdateBus() {
        Bus existingBus = new Bus();
        existingBus.setBusId(1);
        existingBus.setRouteFrom("City A");
        existingBus.setRouteTo("City B");
        existingBus.setJourneyDate(LocalDate.now().plusDays(2));
        existingBus.setTotalSeats(40);
        existingBus.setAvailableSeats(40);
        existingBus.setBusType("Non-AC");

        Bus updatedBus = new Bus();
        updatedBus.setBusId(1);
        updatedBus.setRouteFrom("City X");
        updatedBus.setRouteTo("City Y");
        updatedBus.setJourneyDate(LocalDate.now().plusDays(3));
        updatedBus.setTotalSeats(50);
        updatedBus.setAvailableSeats(50);
        updatedBus.setBusType("AC");

        Route route = new Route("City X", "City Y", 150);
        Mockito.when(routeRepository.findByRouteFromAndRouteTo(Mockito.anyString(), Mockito.anyString())).thenReturn(route);
        Mockito.when(busRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(existingBus));
        Mockito.when(busRepository.save(Mockito.any())).thenReturn(updatedBus);

        try {
            Bus resultBus = busService.updateBus(1, updatedBus);
            Assertions.assertEquals("City X", resultBus.getRouteFrom());
            Assertions.assertEquals("City Y", resultBus.getRouteTo());
            Assertions.assertEquals(50, resultBus.getTotalSeats());
            Assertions.assertEquals("AC", resultBus.getBusType());
        } catch (BusException e) {
            Assertions.fail("BusException should not be thrown");
        }
    }



    @Test
    void testViewAllBuses() {
        List<Bus> buses = new ArrayList<>();
        buses.add(new Bus());
        Mockito.when(busRepository.findAll()).thenReturn(buses);

        try {
            List<Bus> busList = busService.viewAllBus();
            Assertions.assertEquals(1, busList.size());
        } catch (BusException e) {
            Assertions.fail("BusException should not be thrown");
        }
    }

    @Test
    void testViewBusType() {
        List<Bus> buses = new ArrayList<>();
        buses.add(new Bus());
        Mockito.when(busRepository.findByBusType(Mockito.anyString())).thenReturn(buses);

        try {
            List<Bus> busList = busService.viewBusByType("AC");
            Assertions.assertEquals(1, busList.size());
        } catch (BusException e) {
            Assertions.fail("BusException should not be thrown");
        }
    }




    @Test
    void deleteBus_shouldDeleteBusSuccessfully() throws BusException {
        // Arrange
        Integer busId = 1;
        Bus bus = new Bus();
        bus.setBusId(busId);
        bus.setJourneyDate(LocalDate.now().plusDays(1));  // future date

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        doNothing().when(busRepository).delete(bus);

        Bus result = busService.deleteBus(busId);

        assertNotNull(result);
        assertEquals(bus, result);

        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, times(1)).delete(bus);
    }




}
