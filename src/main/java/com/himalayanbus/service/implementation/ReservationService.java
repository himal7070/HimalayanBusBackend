package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.*;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IReservationRepository;
import com.himalayanbus.persistence.repository.IRouteRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.service.IReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IBusRepository busRepository;
    private final IUserRepository userRepository;
    private final IRouteRepository routeRepository;
    private final AccessToken requestAccessToken;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with the provided token";

    public ReservationService(IReservationRepository reservationRepository, IBusRepository busRepository,
                              IUserRepository userRepository, IRouteRepository routeRepository,
                              AccessToken requestAccessToken) {
        this.reservationRepository = reservationRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.requestAccessToken = requestAccessToken;
    }




    @Override
    @Transactional
    public Reservation addReservation(ReservationDTO dto) throws ReservationException {
        User user = getUserFromToken();

        Route route = findRoute(dto.getDepartureLocation(), dto.getDestination());
        Bus bus = findBusForRoute(route, dto.getBookedSeat());

        Reservation reservation = createReservation(dto, user, bus);

        updateBusAvailability(bus, dto.getBookedSeat());

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Long rid, ReservationDTO dto) throws ReservationException {
        User user = getUserFromToken();

        List<Reservation> userReservations = viewReservationsForCurrentUser();

        Reservation reservationToUpdate = userReservations.stream()
                .filter(reservation -> reservation.getReservationID().equals(rid))
                .findFirst()
                .orElseThrow(() -> new ReservationException("Reservation not found for the given reservation ID"));

        Route route = findRoute(dto.getDepartureLocation(), dto.getDestination());
        Bus bus = findBusForRoute(route, dto.getBookedSeat());

        updateReservationDetails(reservationToUpdate, dto, bus);

        updatePassengerDetailsForReservation(reservationToUpdate, user);

        return reservationRepository.save(reservationToUpdate);
    }

    private void updatePassengerDetailsForReservation(Reservation reservation, User user) {
        Passenger passenger = user.getPassenger();
        if (passenger != null) {
            reservation.setPassenger(passenger);
        }
    }

    @Override
    @Transactional
    public Reservation viewReservation(Long rid) throws ReservationException {
        getUserFromToken();

        return reservationRepository.findById(rid)
                .orElseThrow(() -> new ReservationException("Reservation with given id is not found"));
    }

    @Override
    @Transactional
    public List<Reservation> getAllReservation() throws ReservationException {
        getUserFromToken();

        List<Reservation> reservations = reservationRepository.findAll();

        if (reservations.isEmpty()) {
            throw new ReservationException("Reservation Not found");
        }

        return reservations;
    }

    @Override
    @Transactional
    public List<Reservation> viewReservationsForCurrentUser() throws ReservationException {
        Optional<User> userOptional = Optional.ofNullable(getUserFromToken());

        User currentUser = userOptional.orElseThrow(() -> new ReservationException(USER_NOT_FOUND_MESSAGE));

        Passenger passenger = currentUser.getPassenger();
        if (passenger != null) {
            return passenger.getReservationList();
        } else {
            throw new ReservationException("No reservations found for the current user");
        }
    }






    @Override
    @Transactional
    public List<Reservation> viewReservationByUserId(Long uid) throws ReservationException {
        Optional<User> optionalUser = userRepository.findById(uid);

        User user = optionalUser.orElseThrow(() -> new ReservationException("User not found with given user id: " + uid));

        Passenger passenger = user.getPassenger();

        if (passenger == null) {
            throw new ReservationException("Passenger details not found for this user");
        }

        List<Reservation> reservations = passenger.getReservationList();

        if (reservations.isEmpty()) {
            throw new ReservationException("Reservations not found for this user");
        }

        return reservations;
    }


    @Override
    @Transactional
    public Reservation deleteReservation(Long rid) throws ReservationException {
        User user = getUserFromToken();
        Reservation reservation = getReservationForUser(user, rid);

        LocalDate currentDate = LocalDate.now();

        validateReservationDeletion(rid, currentDate);

        updateBusAvailability(reservation.getBus(), -reservation.getBookedSeat());
        reservationRepository.delete(reservation);

        return reservation;
    }


    User getUserFromToken() throws ReservationException {
        Long userIdFromToken = requestAccessToken.getPassengerId();
        return userRepository.findById(userIdFromToken)
                .orElseThrow(() -> new ReservationException(USER_NOT_FOUND_MESSAGE));
    }

    private Route findRoute(String departureLocation, String destination) throws ReservationException {
        Route route = routeRepository.findByRouteFromAndRouteTo(departureLocation, destination);

        if (route == null) {
            throw new ReservationException("Route not found for the given starting to destination");
        }

        return route;
    }

    private Bus findBusForRoute(Route route, int bookedSeats) throws ReservationException {
        Bus bus = busRepository.findByRoute(route);

        if (bus == null) {
            throw new ReservationException("Bus not found for the given starting to destination");
        }

        int availableSeats = bus.getAvailableSeats();

        if (availableSeats < bookedSeats) {
            throw new ReservationException("Only " + availableSeats + " seats are available");
        }

        return bus;
    }

    private Reservation createReservation(ReservationDTO dto, User user, Bus bus) throws ReservationException {
        Reservation reservation = new Reservation();

        if (dto.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Journey Date should be in the future");
        }

        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDestination(dto.getDestination());
        reservation.setDate(dto.getJourneyDate());
        reservation.setStatus("Successful");
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.now());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setBus(bus);
        reservation.setFare(bus.getFare() * dto.getBookedSeat());
        reservation.setBookedSeat(dto.getBookedSeat());
        reservation.setUser(user);

        Passenger passenger = user.getPassenger();
        if (passenger == null) {
            throw new ReservationException("Passenger details not found for the user");
        }

        reservation.setPassenger(passenger);

        return reservation;
    }

    private void updateBusAvailability(Bus bus, int bookedSeats) {
        int availableSeats = bus.getAvailableSeats() - bookedSeats;
        bus.setAvailableSeats(availableSeats);
    }

    private Reservation getReservationForUser(User user, Long reservationId) throws ReservationException {
        Passenger passenger = user.getPassenger();

        if (passenger != null) {
            for (Reservation reservation : passenger.getReservationList()) {
                if (reservation.getReservationID().equals(reservationId)) {
                    return reservation;
                }
            }
            throw new ReservationException("Reservation not found for the given reservation ID");
        }

        throw new ReservationException("Passenger details not found for the given user");
    }




    private void updateReservationDetails(Reservation reservation, ReservationDTO dto, Bus bus) throws ReservationException {
        if (dto.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Journey Date should be in the future");
        }

        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDestination(dto.getDestination());
        reservation.setDate(dto.getJourneyDate());
        reservation.setTime(LocalTime.now());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setBus(bus);
        reservation.setFare(bus.getFare() * dto.getBookedSeat());
        reservation.setBookedSeat(dto.getBookedSeat());
    }


    public void validateReservationDeletion(Long reservationId, LocalDate currentDate) throws ReservationException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("Reservation not found"));

        if (reservation.getJourneyDate() != null && reservation.getJourneyDate().isBefore(currentDate)) {
            throw new ReservationException("Cannot delete past reservations");
        }


    }


    @Override
    @Transactional(readOnly = true)
    public long countActiveReservationsForToday() throws ReservationException {
        LocalDate currentDate = LocalDate.now();

        long activeReservationsCount = reservationRepository.countReservationsByDate(currentDate);

        if (activeReservationsCount == 0) {
            throw new ReservationException("No active reservations for today");
        }

        return activeReservationsCount;
    }


}
