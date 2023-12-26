package com.himalayanbus.controller;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.service.IBusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusControllerTest {

    @Mock
    private IBusService busService;

    @InjectMocks
    private BusController busController;

    @Test
    void testAddBus() throws BusException {
        // Arrange
        Bus busToAdd = createSampleBus();
        when(busService.addBus(any())).thenReturn(busToAdd);

        // Act
        ResponseEntity<Bus> responseEntity = busController.addBus(busToAdd);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(busToAdd, responseEntity.getBody());
    }

    @Test
    void testViewAllBuses() throws BusException {
        // Arrange
        List<Bus> busList = new ArrayList<>();
        when(busService.viewAllBus()).thenReturn(busList);

        // Act
        ResponseEntity<List<Bus>> responseEntity = busController.viewAllBuses();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(busList, responseEntity.getBody());
    }

    @Test
    void testUpdateBus() throws BusException {
        // Arrange
        Long busId = 1L;
        Bus updatedBus = createSampleBus();
        when(busService.updateBus(eq(busId), any())).thenReturn(updatedBus);

        // Act
        ResponseEntity<Bus> responseEntity = busController.updateBus(busId, updatedBus);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedBus, responseEntity.getBody());
    }

    @Test
    void testDeleteBus() throws BusException {
        // Arrange
        Long busIdToDelete = 1L;
        // method to throw BusException
        doThrow(new BusException("Failed to delete bus")).when(busService).deleteBus(busIdToDelete);

        // Act
        ResponseEntity<String> responseEntity = busController.deleteBus(busIdToDelete);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertTrue(Objects.requireNonNull(responseEntity.getBody()).startsWith("Failed to delete bus"));
    }




    @Test
    void testViewBus() throws BusException {
        // Arrange
        Long busIdToView = 1L;
        Bus busToView = createSampleBus();
        when(busService.viewBus(busIdToView)).thenReturn(busToView);

        // Act
        ResponseEntity<Bus> responseEntity = busController.viewBus(busIdToView);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(busToView, responseEntity.getBody());
    }

    @Test
    void testViewBusByType() throws BusException {
        // Arrange
        String busType = "Luxury";
        List<Bus> busListByType = new ArrayList<>();
        when(busService.viewBusByType(busType)).thenReturn(busListByType);

        // Act
        ResponseEntity<List<Bus>> responseEntity = busController.viewBusByType(busType);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(busListByType, responseEntity.getBody());
    }

    @Test
    void testGetTotalBusCount() throws BusException {
        // Arrange
        long busCount = 10L;
        when(busService.countAllBuses()).thenReturn(busCount);

        // Act
        ResponseEntity<String> responseEntity = busController.getTotalBusCount();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Total : " + busCount, responseEntity.getBody());
    }

    @Test
    void testSearchBusByRoute() throws BusException {
        // Arrange
        String routeFrom = "City A";
        String routeTo = "City B";
        List<Bus> busListByRoute = new ArrayList<>();
        when(busService.searchBusByRoute(eq(routeFrom), eq(routeTo), any())).thenReturn(busListByRoute);

        // Act
        ResponseEntity<List<Bus>> responseEntity = busController.searchBusByRoute(routeFrom, routeTo, LocalDate.now());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(busListByRoute, responseEntity.getBody());
    }

    @Test
    void testDelayBusDeparture() throws BusException {
        // Arrange
        Long busId = 1L;
        Long delayMinutes = 30L;
        when(busService.delayBusDeparture(eq(busId), any())).thenReturn("Delay successful");

        // Act
        ResponseEntity<String> responseEntity = busController.delayBusDeparture(busId, delayMinutes);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Delay successful", responseEntity.getBody());
    }

    private Bus createSampleBus() {
        Bus bus = new Bus();
        bus.setBusId(1L);
        bus.setBusName("Himalayan Bus");
        bus.setDriverName("its don");
        bus.setBusType("Luxury");
        bus.setRouteFrom("City A");
        bus.setRouteTo("City B");
        bus.setJourneyDate(LocalDate.now());
        bus.setArrivalTime(LocalTime.of(12, 0));
        bus.setDepartureTime(LocalTime.of(8, 0));
        bus.setTotalSeats(50);
        bus.setAvailableSeats(50);
        bus.setFare(100);
        return bus;
    }
}
