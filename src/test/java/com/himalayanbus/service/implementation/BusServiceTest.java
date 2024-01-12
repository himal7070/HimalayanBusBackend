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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        busToDelete.setTotalSeats(40);

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
        busToDelete.setTotalSeats(40);

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
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Create a sample bus list with one entry
        List<Bus> buses = new ArrayList<>();
        Bus futureBus = new Bus();
        futureBus.setJourneyDate(LocalDate.now().plusDays(1));
        futureBus.setDepartureTime(LocalTime.now().plusHours(1));
        buses.add(futureBus);

        // Mocking the behavior of the busRepository.findAll() method to return the sample list
        when(busRepository.findAll()).thenReturn(buses);

        try {
            List<Bus> result = busService.viewAllBus();
            assertNotNull(result);
            assertEquals(1, result.size());

            // Verifying that the findAll method was called once
            verify(busRepository, times(1)).findAll();

            Bus retrievedBus = result.get(0);
            LocalDateTime departureDateTime = LocalDateTime.of(retrievedBus.getJourneyDate(), retrievedBus.getDepartureTime());
            assertTrue(departureDateTime.isAfter(currentDateTime) || departureDateTime.toLocalDate().isEqual(LocalDate.now()));
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



    @Test
    void testUpdateBusDetails_WhenArrivalTimeNotNull_UpdatesArrivalTime() {
        Bus existingBus = new Bus();
        Bus newBusDetails = new Bus();
        newBusDetails.setArrivalTime(LocalTime.now());

        busService.updateBusDetails(existingBus, newBusDetails);

        assertEquals(newBusDetails.getArrivalTime(), existingBus.getArrivalTime());
        assertNull(existingBus.getBusName());

    }


    @Test
    void testUpdateBusDetails_WhenAvailableSeatsNotNull_UpdatesAvailableSeats() {
        Bus existingBus = new Bus();
        Bus newBusDetails = new Bus();
        newBusDetails.setAvailableSeats(50);

        busService.updateBusDetails(existingBus, newBusDetails);

        assertEquals(newBusDetails.getAvailableSeats(), existingBus.getAvailableSeats());
        assertNull(existingBus.getArrivalTime());
    }




    @Test
    void testUpdateBus_WhenScheduledSeatsExist_ThrowsException() {
        Bus existingBus = new Bus();
        existingBus.setAvailableSeats(30);
        existingBus.setTotalSeats(40);

        Bus newBusDetails = new Bus();
        newBusDetails.setAvailableSeats(35);

        when(busRepository.findById(anyLong())).thenReturn(Optional.of(existingBus));

        assertThrows(BusException.class, () -> busService.updateBus(1L, newBusDetails));
    }



    @Test
    void testCountAllBuses_BusesExist() {
        long expectedCount = 10;

        when(busRepository.count()).thenReturn(expectedCount);

        try {
            long count = busService.countAllBuses();
            assertEquals(expectedCount, count);
            verify(busRepository, times(1)).count();
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testCountAllBuses_NoBusesAvailable() {
        when(busRepository.count()).thenReturn(0L);

        assertThrows(BusException.class, () -> busService.countAllBuses());
        verify(busRepository, times(1)).count();
    }


    @Test
    void testSearchBusByRoute() {
        // Given
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDate journeyDate = LocalDate.now().plusDays(3);

        List<Bus> mockBusList = new ArrayList<>();
        Bus bus1 = new Bus();
        bus1.setRouteFrom("City A");
        bus1.setRouteTo("City B");
        bus1.setJourneyDate(journeyDate);
        bus1.setDepartureTime(LocalTime.NOON);
        mockBusList.add(bus1);

        Bus bus2 = new Bus();
        bus2.setRouteFrom("City A");
        bus2.setRouteTo("City B");
        bus2.setJourneyDate(journeyDate);
        bus2.setDepartureTime(LocalTime.of(14, 30));
        mockBusList.add(bus2);

        when(busRepository.findByRoute_RouteFromContainingAndRoute_RouteToContaining("City A", "City B")).thenReturn(mockBusList);

        List<Bus> result;
        try {
            result = busService.searchBusByRoute(Optional.of("City A"), Optional.of("City B"), journeyDate);
            assertNotNull(result);
            assertFalse(result.isEmpty());

            List<Bus> filteredBuses = result.stream()
                    .filter(bus -> bus.getJourneyDate().equals(journeyDate))
                    .filter(bus -> bus.getDepartureTime().atDate(bus.getJourneyDate()).isAfter(currentDateTime))
                    .toList();

            assertFalse(filteredBuses.isEmpty());
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }





    @Test
    void testUpdateBusDetails_SetBusName() {
        Bus existingBus = new Bus();
        Bus newBusDetails = new Bus();
        newBusDetails.setBusName("New Bus Name");

        // Call updateBusDetails method
        busService.updateBusDetails(existingBus, newBusDetails);

        // Assert that the bus name is updated
        assertEquals("New Bus Name", existingBus.getBusName());
    }

    @Test
    void testUpdateBusDetails_SetDepartureTime() {
        Bus existingBus = new Bus();
        Bus newBusDetails = new Bus();
        LocalTime departureTime = LocalTime.of(9, 0);
        newBusDetails.setDepartureTime(departureTime);

        // Call updateBusDetails method
        busService.updateBusDetails(existingBus, newBusDetails);

        // Assert that the departure time is updated
        assertEquals(departureTime, existingBus.getDepartureTime());
    }

    @Test
    void testUpdateBusDetails_SetDriverName() {
        Bus existingBus = new Bus();
        Bus newBusDetails = new Bus();
        newBusDetails.setDriverName("New Driver Name");

        // Call updateBusDetails method
        busService.updateBusDetails(existingBus, newBusDetails);

        // Assert that the driver name is updated
        assertEquals("New Driver Name", existingBus.getDriverName());
    }




    @Test
    void testDelayBusDeparture_WhenBusFoundAndDelayValid_UpdatesDepartureAndArrivalTime() {
        // Arrange
        Long busId = 1L;
        Bus busToUpdate = new Bus();
        busToUpdate.setBusId(busId);
        LocalTime initialDepartureTime = LocalTime.of(8, 0);
        busToUpdate.setDepartureTime(initialDepartureTime);
        LocalTime initialArrivalTime = LocalTime.of(12, 0);
        busToUpdate.setArrivalTime(initialArrivalTime);
        LocalDate journeyDate = LocalDate.now().plusDays(1);
        busToUpdate.setJourneyDate(journeyDate);

        Duration delayDuration = Duration.ofMinutes(30);

        // Mock behavior for busRepository
        when(busRepository.findById(busId)).thenReturn(Optional.of(busToUpdate));
        when(busRepository.save(any(Bus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        try {
            String result = busService.delayBusDeparture(busId, delayDuration);

            // Assert
            assertEquals("Bus with ID 1 has been updated successfully!", result);
            verify(busRepository, times(1)).findById(busId);
            verify(busRepository, times(1)).save(any(Bus.class));

            // Check updated times
            assertEquals(initialDepartureTime.plus(delayDuration), busToUpdate.getDepartureTime());
            assertEquals(initialArrivalTime.plus(delayDuration), busToUpdate.getArrivalTime());
        } catch (BusException e) {
            fail("BusException should not be thrown");
        }
    }

    @Test
    void testDelayBusDeparture_WhenBusIdNotFound_ThrowBusException() {
        // Arrange
        Long busId = 1L;
        Duration delayDuration = Duration.ofMinutes(30);

        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BusException.class, () -> busService.delayBusDeparture(busId, delayDuration),
                "BusException should be thrown when the bus ID is not found");

        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, never()).save(any());
    }

    @Test
    void testDelayBusDeparture_WhenJourneyDatePassed_ThrowBusException() {
        // Arrange
        Long busId = 1L;
        Duration delayDuration = Duration.ofMinutes(30);
        Bus busToUpdate = new Bus();
        busToUpdate.setBusId(busId);
        busToUpdate.setJourneyDate(LocalDate.now().minusDays(1));

        when(busRepository.findById(busId)).thenReturn(Optional.of(busToUpdate));

        // Act & Assert
        assertThrows(BusException.class, () -> busService.delayBusDeparture(busId, delayDuration),
                "BusException should be thrown when the journey date has passed");

        verify(busRepository, times(1)).findById(busId);
        verify(busRepository, never()).save(any());
    }





}
