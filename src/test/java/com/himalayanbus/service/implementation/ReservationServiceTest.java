package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.*;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IReservationRepository;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private IReservationRepository reservationRepository;

    @Mock
    private IBusRepository busRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRouteRepository routeRepository;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private ReservationService reservationService;

    private static final Long VALID_USER_ID = 123L;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(
                reservationRepository,
                busRepository,
                userRepository,
                routeRepository,
                accessToken
        );
        when(accessToken.getPassengerId()).thenReturn(VALID_USER_ID);


        User mockUser = new User();
        when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(mockUser));


    }


    @Test
    void testGetUserFromToken_ValidToken_ReturnsUser() {
        try {
            User user = reservationService.getUserFromToken();
            assertNotNull(user);
        } catch (ReservationException e) {
            fail("Exception should not be thrown for a valid token");
        }
    }



    @Test
    void testAddReservation_Successful() throws ReservationException {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Location1");
        reservationDTO.setDestination("Location2");
        reservationDTO.setJourneyDate(LocalDate.now());
        reservationDTO.setBookedSeat(2);

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        Passenger passenger = new Passenger();
        passenger.setFirstName("Himal");
        passenger.setLastName("Aryal");
        mockUser.setPassenger(passenger);

        Route mockRoute = new Route();
        mockRoute.setRouteFrom("Location1");
        mockRoute.setRouteTo("Location2");

        Bus mockBus = new Bus();
        mockBus.setBusId(1L);
        mockBus.setAvailableSeats(50);
        mockBus.setFare(10);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(mockRoute);
        when(busRepository.findByRoute(any())).thenReturn(mockBus);

        Reservation mockReservation = new Reservation();
        mockReservation.setDepartureLocation("Location1");
        mockReservation.setDestination("Location2");
        mockReservation.setDate(LocalDate.now());
        mockReservation.setStatus("Successful");
        mockReservation.setDate(LocalDate.now());
        mockReservation.setTime(LocalTime.now());
        mockReservation.setJourneyDate(LocalDate.now());
        mockReservation.setBus(mockBus);
        mockReservation.setFare(mockBus.getFare() * 2);
        mockReservation.setBookedSeat(2);
        mockReservation.setUser(mockUser);

        when(reservationRepository.save(any())).thenReturn(mockReservation);

        Reservation result = reservationService.addReservation(reservationDTO);

        assertNotNull(result);
    }


    @Test
    void testUpdateReservation_Successful() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Location1");
        reservationDTO.setDestination("Location2");
        reservationDTO.setJourneyDate(LocalDate.now());
        reservationDTO.setBookedSeat(2);

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        Passenger passenger = new Passenger();
        passenger.setFirstName("Himal");
        passenger.setLastName("Aryal");
        mockUser.setPassenger(passenger);

        Route mockRoute = new Route();
        mockRoute.setRouteFrom("Location1");
        mockRoute.setRouteTo("Location2");

        Bus mockBus = new Bus();
        mockBus.setBusId(1L);
        mockBus.setAvailableSeats(50);
        mockBus.setFare(10);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(reservationRepository.findById((1L))).thenReturn(Optional.empty());
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(mockRoute);
        when(busRepository.findByRoute(any())).thenReturn(mockBus);

        assertThrows(ReservationException.class, () -> reservationService.updateReservation(1L, reservationDTO));
    }





    @Test
    void testViewReservationsForCurrentUser_NoPassengerDetails() {
        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");


        when(accessToken.getPassengerId()).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        // Asserting the exception
        assertThrows(ReservationException.class, () -> reservationService.viewReservationsForCurrentUser());

        // Verifying that the repository methods were called
        verify(accessToken, times(1)).getPassengerId();
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(reservationRepository);
    }


    @Test
    void testAddReservation_PassengerDetailsNotFound() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Location1");
        reservationDTO.setDestination("Location2");
        reservationDTO.setJourneyDate(LocalDate.now());
        reservationDTO.setBookedSeat(2);

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");



        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(routeRepository.findByRouteFromAndRouteTo(anyString(), anyString())).thenReturn(new Route());
        when(busRepository.findByRoute(any())).thenReturn(new Bus());


        // Asserting the exception
        assertThrows(ReservationException.class, () -> reservationService.addReservation(reservationDTO));

        verify(userRepository, times(1)).findById(anyLong());
        verify(routeRepository, times(1)).findByRouteFromAndRouteTo(anyString(), anyString());
        verify(busRepository, times(1)).findByRoute(any());
        verifyNoInteractions(reservationRepository);
    }





    @Test
    void testViewReservation_Successful() throws ReservationException {
        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        when(accessToken.getPassengerId()).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(new Reservation()));

        Reservation result = reservationService.viewReservation(1L);

        assertNotNull(result);
    }


    @Test
    void testGetAllReservation_Successful() throws ReservationException {
        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        when(accessToken.getPassengerId()).thenReturn(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation()));

        List<Reservation> result = reservationService.getAllReservation();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testViewReservationByUserId_Successful() throws ReservationException {
        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        Passenger passenger = new Passenger();
        passenger.setReservationList(List.of(new Reservation()));
        mockUser.setPassenger(passenger);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));

        List<Reservation> result = reservationService.viewReservationByUserId(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }




    @Test
    void testUpdateReservationDetails_ValidJourneyDate() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setJourneyDate(LocalDate.now().plusDays(1));
        ReservationDTO dto = new ReservationDTO();
        dto.setJourneyDate(LocalDate.now().plusDays(2));
        Bus bus = new Bus();
        bus.setFare(50);
        dto.setBookedSeat(2);


        Method method = ReservationService.class.getDeclaredMethod("updateReservationDetails", Reservation.class, ReservationDTO.class, Bus.class);
        method.setAccessible(true);

        ReservationService reservationService = new ReservationService(
                reservationRepository,
                busRepository,
                userRepository,
                routeRepository,
                accessToken
        );
        method.invoke(reservationService, reservation, dto, bus);

        // Verify
        assertEquals(dto.getDepartureLocation(), reservation.getDepartureLocation());
        assertEquals(dto.getDestination(), reservation.getDestination());
        assertEquals(dto.getJourneyDate(), reservation.getJourneyDate());
        assertNotNull(reservation.getTime());
        assertEquals(bus, reservation.getBus());
        assertEquals(100, reservation.getFare());
        assertEquals(2, reservation.getBookedSeat());
    }


    @Test
    void testUpdateReservationDetails_InvalidJourneyDate() throws Exception {
        Reservation reservation = new Reservation();
        ReservationDTO dto = new ReservationDTO();
        dto.setJourneyDate(LocalDate.now().minusDays(1));
        Bus bus = new Bus();

        Method method = ReservationService.class.getDeclaredMethod("updateReservationDetails", Reservation.class, ReservationDTO.class, Bus.class);
        method.setAccessible(true);

        ReservationService reservationService = new ReservationService(
                reservationRepository,
                busRepository,
                userRepository,
                routeRepository,
                accessToken
        );

        try {
            method.invoke(reservationService, reservation, dto, bus);
            fail("Expected an exception to be thrown");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();

            assertTrue(cause instanceof ReservationException);
        }
    }


    @Test
    void testViewReservationByUserId() {
        Long userId = 1L;
        User user = new User();
        user.setUserID(userId);

        Passenger passenger = new Passenger();

        Reservation reservation1 = new Reservation();
        Reservation reservation2 = new Reservation();
        passenger.setReservationList(List.of(reservation1, reservation2));
        user.setPassenger(passenger);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        try {
            List<Reservation> reservations = reservationService.viewReservationByUserId(userId);
            assertEquals(2, reservations.size());
            assertTrue(reservations.contains(reservation1));
            assertTrue(reservations.contains(reservation2));
        } catch (ReservationException e) {
            fail("Exception thrown: " + e.getMessage());
        }

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testViewReservationByUserId_UserNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ReservationException.class, () -> reservationService.viewReservationByUserId(userId));

        verify(userRepository, times(1)).findById(userId);
    }



    @Test
    void testDeleteReservation_UserNotFound_ThrowsException() {
        doThrow(new RuntimeException("User not found with the provided token"))
                .when(userRepository).findById(anyLong());

        assertThrows(RuntimeException.class, () -> reservationService.deleteReservation(1L));

        verify(reservationRepository, never()).delete(any());
    }



    @Test
    void testValidateReservationDeletion_ReservationNotExpired() throws Exception {
        long reservationId = 1L;
        Reservation reservation = new Reservation();
        reservation.setReservationID(reservationId);
        reservation.setJourneyDate(LocalDate.now().plusDays(1));

        // Mock behavior
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Invoke the method under test
        Method method = ReservationService.class.getDeclaredMethod("validateReservationDeletion", Long.class, LocalDate.class);
        method.setAccessible(true);

        ReservationService reservationService = new ReservationService(
                reservationRepository,
                busRepository,
                userRepository,
                routeRepository,
                accessToken
        );

        try {

            method.invoke(reservationService, reservationId, LocalDate.now());
        } catch (InvocationTargetException e) {
            fail("Unexpected exception: " + e.getCause().getMessage());
        }
    }





    @Test
    void testValidateReservationDeletion_ReservationExpired() throws Exception {
        Method method = ReservationService.class.getDeclaredMethod("validateReservationDeletion", Long.class, LocalDate.class);
        method.setAccessible(true);

        Reservation reservation = new Reservation();
        reservation.setJourneyDate(LocalDate.now().minusDays(1));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        ReservationService reservationService = new ReservationService(
                reservationRepository,
                busRepository,
                userRepository,
                routeRepository,
                accessToken
        );

        assertThrows(ReservationException.class, () -> {
            try {
                method.invoke(reservationService, reservation.getReservationID(), LocalDate.now());
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        });
    }














    @Test
    void testDeleteReservation_ExpiredReservation() {
        Reservation reservation = new Reservation();
        reservation.setJourneyDate(LocalDate.now().minusDays(1));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        assertThrows(ReservationException.class, () -> reservationService.deleteReservation(1L));

        verify(reservationRepository, never()).delete(any());
    }



    @Test
    void testGetUserFromToken_InvalidToken_ThrowsException() {
        when(accessToken.getPassengerId()).thenReturn(null);

        assertThrows(ReservationException.class, () -> reservationService.getUserFromToken());

        verify(userRepository, never()).findById(anyLong());
    }





    @Test
    void testCreateReservation_PassengerDetailsNotFound_ThrowsException() {
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setDepartureLocation("Location1");
        reservationDTO.setDestination("Location2");
        reservationDTO.setJourneyDate(LocalDate.now());
        reservationDTO.setBookedSeat(2);

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        assertThrows(ReservationException.class, () -> reservationService.addReservation(reservationDTO));

        verify(reservationRepository, never()).save(any());
    }



    @Test
    void testAddReservation_InsufficientAvailableSeats_ThrowsException() {
        ReservationDTO reservationDTO = new ReservationDTO();

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        Route mockRoute = new Route();
        mockRoute.setRouteFrom("Location1");
        mockRoute.setRouteTo("Location2");

        Bus mockBus = new Bus();
        mockBus.setAvailableSeats(1);


        assertThrows(ReservationException.class, () -> reservationService.addReservation(reservationDTO));

        verify(busRepository, never()).save(any());
    }


    @Test
    void testUpdateReservation_InsufficientAvailableSeats_ThrowsException() {
        ReservationDTO reservationDTO = new ReservationDTO();
        // Set up reservationDTO...

        User mockUser = new User();
        mockUser.setUserID(1L);
        mockUser.setEmail("darkCoder");

        Route mockRoute = new Route();
        mockRoute.setRouteFrom("Location1");
        mockRoute.setRouteTo("Location2");

        Bus mockBus = new Bus();
        mockBus.setAvailableSeats(1);


        assertThrows(ReservationException.class, () -> reservationService.updateReservation(1L, reservationDTO));

        verify(busRepository, never()).save(any());
    }



    @Test
    void testDeleteReservation_ReservationNotFoundForUser_ThrowsException() {
        User mockUser = getMockUser();

        // Mock repository method calls
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Perform the test
        assertThrows(ReservationException.class, () -> reservationService.deleteReservation(1L));

        // Verify that the delete method was never called on the repository
        verify(reservationRepository, never()).delete(any());
    }

    private static User getMockUser() {
        User mockUser = new User();
        mockUser.setUserID(1L);

        Passenger passenger = new Passenger();
        Reservation reservation1 = new Reservation();
        reservation1.setReservationID(1L);
        Reservation reservation2 = new Reservation();
        reservation2.setReservationID(2L);
        List<Reservation> passengerReservations = List.of(reservation1, reservation2);
        passenger.setReservationList(passengerReservations);
        mockUser.setPassenger(passenger);
        return mockUser;
    }


}
