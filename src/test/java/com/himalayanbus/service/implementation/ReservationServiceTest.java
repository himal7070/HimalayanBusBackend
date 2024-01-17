package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.*;
import com.himalayanbus.persistence.repository.*;
import com.himalayanbus.security.token.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IBusRepository busRepository;

    @Mock
    private IPassengerRepository passengerRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRouteRepository routeRepository;

    @Mock
    private AccessToken requestAccessToken;

    @InjectMocks
    private ReservationService reservationService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);


    }

    @Test
    void testGetUserFromToken_ValidToken_ReturnsUserWithPassenger() {
        // Mocking user ID from the access token
        Long userId = 123L;
        when(requestAccessToken.getUserID()).thenReturn(userId);

        User expectedUser = new User();
        expectedUser.setUserID(userId);

        Passenger mockedPassenger = new Passenger();
        expectedUser.setPassenger(mockedPassenger);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        try {

            User actualUser = reservationService.getUserFromToken();

            // Assertions
            assertNotNull(actualUser);
            assertEquals(expectedUser, actualUser);
            assertNotNull(actualUser.getPassenger());
        } catch (ReservationException e) {
            fail("Exception should not be thrown for a valid token");
        }

        // Verify that userRepository.findById was called with the correct userId
        verify(userRepository, times(1)).findById(userId);
    }



    @Test
    void testGetUserFromToken_InvalidToken_ThrowsException() {
        when(requestAccessToken.getUserID()).thenReturn(null);

        assertThrows(ReservationException.class, () -> reservationService.getUserFromToken(),
                "Exception should be thrown for an invalid token");
    }

    @Test
    void testGetUserFromToken_UserNotFound_ThrowsException() {
        Long userId = 123L;
        when(requestAccessToken.getUserID()).thenReturn(userId);

        // Mocking UserRepository to return an empty Optional, simulating user not found
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ReservationException.class, () -> reservationService.getUserFromToken(),
                "Exception should be thrown when user is not found for the given token");
    }

    @Test
    void testGetUserFromToken_PassengerNotFound_ThrowsException() {
        Long userId = 123L;
        when(requestAccessToken.getUserID()).thenReturn(userId);

        User userWithoutPassenger = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutPassenger));

        assertThrows(ReservationException.class, () -> reservationService.getUserFromToken(),
                "Exception should be thrown when passenger is not found for the given user");
    }

    @Test
    void testGetUserFromToken_UserFoundButPassengerIsNull_ThrowsException() {
        Long userId = 123L;
        when(requestAccessToken.getUserID()).thenReturn(userId);


        User userWithNullPassenger = new User();
        userWithNullPassenger.setPassenger(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithNullPassenger));

        assertThrows(ReservationException.class, () -> reservationService.getUserFromToken(),
                "Exception should be thrown when passenger is null for the given user");
    }










    @Test
    void testAddReservation() {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Departure");
        reservationDTO.setDestination("Destination");
        reservationDTO.setJourneyDate(LocalDate.now().plusDays(1));
        reservationDTO.setBookedSeat(2);

        User user = new User();
        Passenger passenger = new Passenger();
        passenger.setUser(user);

        Bus bus = new Bus();
        bus.setFare(10);
        bus.setRoute(new Route());

        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(any())).thenReturn(Optional.of(passenger));
        when(busRepository.findById(any())).thenReturn(Optional.of(bus));
        when(routeRepository.findByRouteFromAndRouteTo(any(), any())).thenReturn(new Route());

        // Call the method
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, 1L));

        // Verify that no data is saved in the repository
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testAddReservation_PassengerDetailsNotFound() {
        // Mocking necessary objects
        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(any())).thenReturn(Optional.empty());

        ReservationDTO reservationDTO = new ReservationDTO(); // Create DTO

        // Call the method and verify the exception
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, 1L));

        verify(busRepository, never()).findById(any());
        verify(routeRepository, never()).findByRouteFromAndRouteTo(any(), any());
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testAddReservation_BusFound() {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        Long busId = 1L;

        Bus bus = new Bus();
        bus.setRoute(new Route());

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(routeRepository.findByRouteFromAndRouteTo(any(), any())).thenReturn(new Route());

        // Call the method
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, busId));

        // Verify that no data is saved in the repository
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testAddReservation_BusNotFound() {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        Long busId = 1L;

        when(busRepository.findById(busId)).thenReturn(Optional.empty());

        // Call the method and verify the exception
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, busId));

        verify(routeRepository, never()).findByRouteFromAndRouteTo(any(), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testAddReservation_RouteMatchesBusRoute() {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Departure");
        reservationDTO.setDestination("Destination");

        Bus bus = new Bus();
        bus.setRoute(new Route());

        when(busRepository.findById(any())).thenReturn(Optional.of(bus));
        when(routeRepository.findByRouteFromAndRouteTo(any(), any())).thenReturn(new Route());

        // Call the method
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, 1L));

        // Verify that no data is saved in the repository
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testAddReservation_CreateReservation() {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        Long busId = 1L;

        Bus bus = new Bus();
        bus.setRoute(new Route());

        when(busRepository.findById(busId)).thenReturn(Optional.of(bus));
        when(routeRepository.findByRouteFromAndRouteTo(any(), any())).thenReturn(new Route());

        // Call the method
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, busId));

        // Verify that no data is saved in the repository
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testCreateReservation() throws ReservationException {
        // Mocking necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Departure");
        reservationDTO.setDestination("Destination");
        reservationDTO.setJourneyDate(LocalDate.now().plusDays(1));
        reservationDTO.setBookedSeat(2);

        Passenger passenger = new Passenger();
        passenger.setUser(new User());

        Bus bus = new Bus();
        bus.setFare(10);

        when(reservationRepository.save(any())).thenReturn(new Reservation());
        when(busRepository.findById(any())).thenReturn(Optional.of(bus));

        // Call the method
        Reservation reservation = reservationService.createReservation(reservationDTO, passenger, bus);

        assertNotNull(reservation);
        assertEquals("Departure", reservation.getDepartureLocation());
        assertEquals("Destination", reservation.getDestination());
        assertEquals(LocalDate.now().plusDays(1), reservation.getJourneyDate());
        assertEquals(2, reservation.getBookedSeat());
        assertEquals(20, reservation.getFare());
        assertNotNull(reservation.getPassenger());
        assertNotNull(reservation.getBus());
        assertEquals("Active", reservation.getStatus());
    }



    @Test
    void testUpdateReservationStatus_NullFields() {
        // Create a reservation with null fields
        Reservation reservationWithNullFields = new Reservation();
        reservationWithNullFields.setBus(null); // Set bus as null

        // Mock the behavior of reservationRepository.findAll()
        when(reservationRepository.findAll()).thenReturn(Collections.singletonList(reservationWithNullFields));

        // Call the method to be tested
        reservationService.updateReservationStatus();

        // Verify that the status remains unchanged (no NullPointerException)
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testScheduledReservationDeletion() {
        // Mock data
        List<Reservation> reservationsToDelete = new ArrayList<>();
        reservationsToDelete.add(new Reservation());
        when(reservationRepository.findByStatusAndDateBefore("Cancelled", LocalDate.now().minusDays(1)))
                .thenReturn(reservationsToDelete);

        // Call the method
        reservationService.scheduledReservationDeletion();

        // Verify interactions with mocks
        verify(reservationRepository, times(1)).deleteAll(reservationsToDelete);
    }


    @Test
    void testCountActiveReservationsForToday() {
        // Mock data
        when(reservationRepository.countReservationsByDate(any(LocalDate.class))).thenReturn(5L);
        when(reservationRepository.countReservationsByDateAndStatus(any(LocalDate.class), eq("Cancelled"))).thenReturn(2L);

        // Call the method
        long result = reservationService.countActiveReservationsForToday();

        // Assertions
        assertEquals(3L, result); // (5 - 2)

        // Verify interactions with mocks
        verify(reservationRepository, times(1)).countReservationsByDate(any(LocalDate.class));
        verify(reservationRepository, times(1)).countReservationsByDateAndStatus(any(LocalDate.class), eq("Cancelled"));
    }


    @Test
    void testAddReservation_NoPassengerDetails_ExceptionThrown() {
        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(any())).thenReturn(Optional.empty());

        ReservationDTO reservationDTO = new ReservationDTO(); // Create DTO

        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, 1L));

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testAddReservation_JourneyDateInPast_ExceptionThrown() {
        // Mock necessary objects
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Departure");
        reservationDTO.setDestination("Destination");
        reservationDTO.setJourneyDate(LocalDate.now().minusDays(1)); // Set past date
        reservationDTO.setBookedSeat(2);

        User user = new User();
        Passenger passenger = new Passenger();
        passenger.setUser(user);

        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(any())).thenReturn(Optional.of(passenger));
        when(busRepository.findById(any())).thenReturn(Optional.of(new Bus()));
        when(routeRepository.findByRouteFromAndRouteTo(any(), any())).thenReturn(new Route());

        // Call the method
        assertThrows(ReservationException.class,
                () -> reservationService.addReservation(reservationDTO, 1L));

        // Verify that no data is saved in the repository
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void testViewReservationsForCurrentUser_NoReservations_ExceptionThrown() {
        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(any())).thenReturn(Optional.of(new Passenger()));

        assertThrows(ReservationException.class,
                () -> reservationService.viewReservationsForCurrentUser());
    }


    @Test
    void testViewReservationsForCurrentUser_ValidUser_ReturnsReservations() throws ReservationException {
        // Mocking necessary objects
        User user = new User();
        Passenger passenger = new Passenger();
        List<Reservation> reservations = new ArrayList<>();
        Reservation reservation1 = new Reservation();
        reservation1.setStatus("Active");
        reservations.add(reservation1);
        Reservation reservation2 = new Reservation();
        reservation2.setStatus("Expired");
        reservations.add(reservation2);
        Reservation reservation3 = new Reservation();
        reservation3.setStatus("Cancelled");
        reservations.add(reservation3);

        user.setPassenger(passenger);
        passenger.setReservationList(reservations);

        // Mock the requestAccessToken and passengerRepository
        AccessToken requestAccessToken = mock(AccessToken.class);
        when(requestAccessToken.getUserID()).thenReturn(1L);
        IPassengerRepository passengerRepository = mock(IPassengerRepository.class);
        when(passengerRepository.findById(anyLong())).thenReturn(Optional.of(passenger));

        // Create an instance of ReservationService manually
        ReservationService reservationService = new ReservationService(
                mock(IReservationRepository.class),
                mock(IBusRepository.class),
                mock(IRouteRepository.class),
                userRepository,
                requestAccessToken
        );

        // Mock the getUserFromToken method within the service instance
        ReservationService spyReservationService = spy(reservationService);
        doReturn(user).when(spyReservationService).getUserFromToken();

        // Call the method
        List<Reservation> result = spyReservationService.viewReservationsForCurrentUser();

        // Assertions
        assertNotNull(result);
        assertEquals(3, result.size());
    }


    @Test
    void testViewReservationsForCurrentUser_NoPassenger_ExceptionThrown() {
        when(requestAccessToken.getUserID()).thenReturn(1L);
        when(passengerRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method and verify the exception
        assertThrows(ReservationException.class, () -> reservationService.viewReservationsForCurrentUser());
    }


    @Test
    void testGetAllReservationWhenNotEmpty() {
        // Mocking repository to return a non-empty list
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());

        when(reservationRepository.findAll()).thenReturn(reservations);

        try {
            List<Reservation> result = reservationService.getAllReservation();
            assertNotNull(result);
            assertEquals(1, result.size());
        } catch (ReservationException e) {
            fail("Should not throw an exception when reservations exist");
        }
    }

    @Test
    void testGetAllReservationWhenEmpty() {
        when(reservationRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(ReservationException.class, () -> {
            reservationService.getAllReservation();
        });
    }


    @Test
    void testFindRoute_RouteFound() {
        // Mock necessary objects
        String departureLocation = "Location1";
        String destination = "Location2";
        Route route = new Route();

        when(routeRepository.findByRouteFromAndRouteTo(departureLocation, destination)).thenReturn(route);

        try {
            Route foundRoute = reservationService.findRoute(departureLocation, destination);
            assertNotNull(foundRoute);
        } catch (ReservationException e) {
            fail("Should not throw an exception when the route is found");
        }
    }


    @Test
    void testFindRoute_RouteNotFound() {
        // Mock necessary objects
        String departureLocation = "Location1";
        String destination = "Location2";

        when(routeRepository.findByRouteFromAndRouteTo(departureLocation, destination)).thenReturn(null);

        assertThrows(ReservationException.class, () -> reservationService.findRoute(departureLocation, destination));
    }


    @Test
    void testUpdateBusAvailability_IncreaseSeats() {
        // Mock necessary objects
        Bus bus = new Bus();
        bus.setAvailableSeats(5);
        int bookedSeats = 3;

        reservationService.updateBusAvailability(bus, bookedSeats, true);

        assertEquals(8, bus.getAvailableSeats());
    }


    @Test
    void testUpdateBusAvailability_DecreaseSeats() {
        // Mock necessary objects
        Bus bus = new Bus();
        bus.setAvailableSeats(8);
        int bookedSeats = 3;

        reservationService.updateBusAvailability(bus, bookedSeats, false);

        assertEquals(5, bus.getAvailableSeats());
    }

    @Test
    void testGetReservationForUser_ReservationFound() {
        // Mock necessary objects
        User user = new User();
        Passenger passenger = new Passenger();
        Reservation reservation = new Reservation();
        reservation.setReservationID(1L);
        passenger.setUser(user);
        passenger.setReservationList(Collections.singletonList(reservation));
        user.setPassenger(passenger);

        try {
            Reservation foundReservation = reservationService.getReservationForUser(user, 1L);
            assertNotNull(foundReservation);
            assertEquals(1L, foundReservation.getReservationID());
        } catch (ReservationException e) {
            fail("Should not throw an exception when the reservation is found");
        }
    }

    @Test
    void testGetReservationForUser_ReservationNotFound() {
        // Mocked necessary objects
        User user = new User();
        Passenger passenger = new Passenger();
        Reservation reservation = new Reservation();
        reservation.setReservationID(1L);
        passenger.setUser(user);
        passenger.setReservationList(Collections.singletonList(reservation));
        user.setPassenger(passenger);

        assertThrows(ReservationException.class, () -> reservationService.getReservationForUser(user, 2L));
    }

    @Test
    void testValidateReservationDeletion_PastReservationDate_ExceptionThrown() {
        // Mocked necessary objects
        Reservation reservation = new Reservation();
        reservation.setJourneyDate(LocalDate.now().minusDays(1));

        assertThrows(ReservationException.class, () -> reservationService.validateReservationDeletion(1L, LocalDate.now()));
    }

    @Test
    void testValidateReservationDeletion_FutureReservationDate_NoExceptionThrown() {
        // Mock necessary objects
        Reservation reservation = new Reservation();
        reservation.setJourneyDate(LocalDate.now().plusDays(1));

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        try {
            reservationService.validateReservationDeletion(1L, LocalDate.now());
        } catch (ReservationException e) {
            fail("Should not throw an exception when the reservation date is in the future. Exception message: " + e.getMessage(), e);
        }

        // Verify that findById was called with the correct ID
        verify(reservationRepository).findById(1L);
    }


    @Test
    void testUpdateReservationStatus() {
        // Prepare mock data
        Bus bus = new Bus();
        bus.setJourneyDate(LocalDate.now());
        bus.setArrivalTime(LocalTime.now());

        Reservation reservation = new Reservation();
        reservation.setBus(bus);
        reservation.setJourneyDate(LocalDate.now());

        List<Reservation> reservations = Collections.singletonList(reservation);


        when(reservationRepository.findAll()).thenReturn(reservations);


        when(reservationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.updateReservationStatus();

        // Verify that the status is updated correctly based on the conditions
        assertEquals("Expired", reservation.getStatus());
    }



}