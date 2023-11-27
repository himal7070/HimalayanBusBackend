package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
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
    void testAddBus() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");
        Route route = new Route();
        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(route);
        when(busRepository.save(bus)).thenReturn(bus);

        try {
            Bus result = busService.addBus(bus);
            assertNotNull(result);
            verify(routeRepository, times(1)).findByRouteFromAndRouteTo("City A", "City B");
            verify(busRepository, times(1)).save(bus);
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testUpdateBus() {
        Bus existingBus = new Bus();
        existingBus.setBusId(1L);
        existingBus.setRouteFrom("City A");
        existingBus.setRouteTo("City B");
        existingBus.setJourneyDate(LocalDate.now().plusDays(2));
        existingBus.setTotalSeats(40);
        existingBus.setAvailableSeats(40);
        existingBus.setBusType("Non-AC");

        Bus updatedBus = new Bus();
        updatedBus.setBusId(1L);
        updatedBus.setRouteFrom("City X");
        updatedBus.setRouteTo("City Y");
        updatedBus.setJourneyDate(LocalDate.now().plusDays(3));
        updatedBus.setTotalSeats(50);
        updatedBus.setAvailableSeats(50);
        updatedBus.setBusType("AC");

        Route route = new Route("City X", "City Y", 150);
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(route);
        when(busRepository.findById(anyLong())).thenReturn(Optional.of(existingBus));
        when(busRepository.save(any())).thenReturn(updatedBus);

        try {
            Bus resultBus = busService.updateBus(1L, updatedBus);
            assertEquals("City X", resultBus.getRouteFrom());
            assertEquals("City Y", resultBus.getRouteTo());
            assertEquals(50, resultBus.getTotalSeats());
            assertEquals("AC", resultBus.getBusType());
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testDeleteBus_WhenNoScheduledSeatsExist_DeletesBus() {
        // Arrange
        Long busId = 1L;
        Bus busToDelete = new Bus();
        busToDelete.setBusId(busId);
        busToDelete.setAvailableSeats(40);
        busToDelete.setTotalSeats(40); // No scheduled seats

        // Mock behavior for busRepository
        when(busRepository.findById(busId)).thenReturn(Optional.of(busToDelete));
        doNothing().when(busRepository).delete(busToDelete);

        try {
            // Act
            Bus deletedBus = busService.deleteBus(busId);

            // Assert
            assertNotNull(deletedBus);
            assertEquals(busToDelete, deletedBus);

            // Verify method calls
            verify(busRepository, times(1)).findById(busId);
            verify(busRepository, times(1)).delete(busToDelete);
        } catch (BusException e) {
            fail("BusException should not be thrown when no scheduled seats exist");
        }
    }

    @Test
    void testDeleteBus_WhenScheduledSeatsExist_ThrowsException() {
        // Arrange
        Long busId = 1L;
        Bus busToDelete = new Bus();
        busToDelete.setBusId(busId);
        busToDelete.setAvailableSeats(30);
        busToDelete.setTotalSeats(40); // Scheduled seats exist

        // Mock behavior for busRepository
        when(busRepository.findById(busId)).thenReturn(Optional.of(busToDelete));

        // Act & Assert
        assertThrows(BusException.class, () -> busService.deleteBus(busId),
                "BusException should be thrown when scheduled seats exist");

        // Verify method calls
        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, never()).delete(busToDelete);
    }




    @Test
    void testViewBus() {
        Long busId = 1L;
        Bus bus = new Bus();
        bus.setBusId(busId);

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));

        try {
            Bus result = busService.viewBus(busId);
            assertNotNull(result);
            assertEquals(bus, result);
            verify(busRepository, times(1)).findById(busId);
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testViewBusByType() {
        List<Bus> buses = new ArrayList<>();
        buses.add(new Bus());

        when(busRepository.findByBusType(anyString())).thenReturn(buses);

        try {
            List<Bus> result = busService.viewBusByType("AC");
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(busRepository, times(1)).findByBusType("AC");
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testViewAllBus() {
        List<Bus> buses = new ArrayList<>();
        buses.add(new Bus());

        when(busRepository.findAll()).thenReturn(buses);

        try {
            List<Bus> result = busService.viewAllBus();
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(busRepository, times(1)).findAll();
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }



    @Test
    void testAddBus_WhenRouteNotFound_ThrowBusException() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(null);

        assertThrows(BusException.class, () -> busService.addBus(bus));
    }

    @Test
    void testAddBus_WhenBusAlreadyExistsOnRoute_ThrowBusException() {
        Bus bus = new Bus();
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");
        Route route = new Route();
        route.getBusList().add(bus);

        when(routeRepository.findByRouteFromAndRouteTo("City A", "City B")).thenReturn(route);

        assertThrows(BusException.class, () -> busService.addBus(bus));
    }


    @Test
    void testUpdateBus_WhenBusIdNotFound_ThrowBusException() {
        Long busId = 1L;
        Bus newBusDetails = new Bus();

        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        assertThrows(BusException.class, () -> busService.updateBus(busId, newBusDetails));
    }




    @Test
    void testUpdateBus_WhenScheduledSeatsExist_ThrowBusException() {
        Long busId = 1L;
        Bus existingBus = new Bus();
        existingBus.setAvailableSeats(30);
        existingBus.setTotalSeats(40);

        Bus newBusDetails = new Bus();

        when(busRepository.findById(busId)).thenReturn(Optional.of(existingBus));

        assertThrows(BusException.class, () -> busService.updateBus(busId, newBusDetails));
    }

    @Test
    void testUpdateBus_WhenInvalidRouteDetails_ThrowBusException() {
        Long busId = 1L;
        Bus existingBus = new Bus();
        existingBus.setRouteFrom("City A");
        existingBus.setRouteTo("City B");

        Bus newBusDetails = new Bus();
        newBusDetails.setRouteFrom("Invalid City");
        newBusDetails.setRouteTo("City B");

        when(busRepository.findById(busId)).thenReturn(Optional.of(existingBus));
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(null);

        assertThrows(BusException.class, () -> busService.updateBus(busId, newBusDetails));
    }


    @Test
    void testUpdateBus_CoveringFieldUpdates() {
        Long busId = 1L;
        Bus existingBus = new Bus();
        existingBus.setAvailableSeats(30);
        existingBus.setTotalSeats(40);

        Bus newBusDetails = new Bus();
        newBusDetails.setArrivalTime(LocalTime.now());
        newBusDetails.setAvailableSeats(35);
        newBusDetails.setBusName("New Bus Name");
        newBusDetails.setBusType("AC");
        newBusDetails.setDepartureTime(LocalTime.now());
        newBusDetails.setDriverName("New Driver");
        newBusDetails.setRouteFrom("New City A");
        newBusDetails.setRouteTo("New City B");
        newBusDetails.setTotalSeats(45);

        when(busRepository.findById(busId)).thenReturn(Optional.of(existingBus));
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(new Route());

        BusException exception = assertThrows(BusException.class, () -> busService.updateBus(busId, newBusDetails));
        assertEquals("Cannot update a bus that already has scheduled seats", exception.getMessage());
    }



    @Test
    void testDeleteBus_WhenScheduledSeatsExist_ThrowBusException() {
        Long busId = 1L;
        Bus busToDelete = new Bus();
        busToDelete.setAvailableSeats(30);
        busToDelete.setTotalSeats(40);

        when(busRepository.findById(busId)).thenReturn(Optional.of(busToDelete));

        assertThrows(BusException.class, () -> busService.deleteBus(busId));
    }















}
