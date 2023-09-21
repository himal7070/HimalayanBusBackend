package com.himalayanbus;
import com.himalayanbus.model.Bus;
import com.himalayanbus.persistence.IBusRepository;
import com.himalayanbus.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class HimalayanBusApplicationTests {

	@Test
	void contextLoads() {
	}

	@Mock
	private IBusRepository busRepository;

	@InjectMocks
	private BusService busService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}


	//tHis is just an example to test methods
	@Test
	void testAddBus() {

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






}
