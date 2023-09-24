package com.himalayanbus.service;

import com.himalayanbus.exception.BusException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.IRepository.IBusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


//@SpringBootTest
@AutoConfigureMockMvc
class BusServiceTest {

    @Mock
    private IBusRepository busRepository;

    @InjectMocks
    private BusService busService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    //tHis is just an example to test methods for now
    @Test
    void addBus() throws BusException {

        // Arrange
        Bus bus = new Bus();
        bus.setBusId(1);
        bus.setBusName("Hello bus");
        bus.setDriverName("Driver");
        bus.setBusType("Type A");
        bus.setRouteFrom("Nepal");
        bus.setRouteTo("Netherlands");
        bus.setJourneyDate(LocalDate.of(2023, 9, 21));
        bus.setArrivalTime(LocalTime.of(10, 0));
        bus.setDepartureTime(LocalTime.of(15, 0));
        bus.setTotalSeats(50);
        bus.setAvailableSeats(30);

        // Mock the behavior of busRepository.save() to return the same object
        when(busRepository.save(bus)).thenReturn(bus);

        // Act
        Bus addedBus = busService.addBus(bus);

        // Assert
        assertNotNull(addedBus);


    }

    @Test
    void viewAllBuses() throws BusException {

        //arrange
        List<Bus> busList = new ArrayList<>();
        busList.add(new Bus());
        when(busRepository.findAll()).thenReturn(busList);

        // Act
        List<Bus> result = busService.viewAllBuses();

        // Assert
        assertEquals(1, result.size());


    }

    @Test
    void updateBus() throws BusException {

        // Arrange
        Bus busToUpdate = new Bus();
        busToUpdate.setBusId(1);
        when(busRepository.findById(1)).thenReturn(Optional.of(busToUpdate));
        when(busRepository.save(busToUpdate)).thenReturn(busToUpdate);

        // Act
        Bus updatedBus = busService.updateBus(busToUpdate);

        // Assert
        assertEquals(busToUpdate, updatedBus);
    }

    @Test
    void deleteBus() throws BusException {

        Integer busIdToDelete = 1;
        Bus existingBus = new Bus();
        existingBus.setBusId(busIdToDelete);
        when(busRepository.findById(busIdToDelete)).thenReturn(Optional.of(existingBus));

        // Act
        Bus deletedBus = busService.deleteBus(busIdToDelete);

        // Assert
        assertEquals(existingBus, deletedBus);
        verify(busRepository, times(1)).delete(existingBus);
    }





}
