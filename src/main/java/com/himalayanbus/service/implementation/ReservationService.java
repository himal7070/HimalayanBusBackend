package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.*;
import com.himalayanbus.persistence.repository.*;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.service.IReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IBusRepository busRepository;
    private final IUserRepository userRepository;
    private final IRouteRepository routeRepository;

    private final IPassengerRepository passengerRepository;
    private final AccessToken requestAccessToken;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found with the provided token";

    public ReservationService(IReservationRepository reservationRepository, IBusRepository busRepository,
                              IUserRepository userRepository, IRouteRepository routeRepository, IPassengerRepository passengerRepository,
                              AccessToken requestAccessToken) {
        this.reservationRepository = reservationRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.passengerRepository = passengerRepository;
        this.requestAccessToken = requestAccessToken;
    }



    @Override
    @Transactional
    public Reservation addReservation(ReservationDTO dto, Long busId) throws ReservationException {
        User user = getUserFromToken();

        Passenger passenger = user.getPassenger();
        if (passenger == null) {
            throw new ReservationException("Passenger details not found for the user");
        }

        Optional<Bus> optionalBus = busRepository.findById(busId);
        Bus bus = optionalBus.orElseThrow(() -> new ReservationException("Bus not found with given bus ID: " + busId));

        Route route = findRoute(dto.getDepartureLocation(), dto.getDestination());

        if (!bus.getRoute().equals(route)) {
            throw new ReservationException("The specified bus is not assigned to the provided route");
        }

        Reservation reservation = createReservation(dto, passenger, bus);

        updateBusAvailability(bus, dto.getBookedSeat(), false);
        updateReservationStatus(reservation, bus);

        return reservationRepository.save(reservation);
    }

    private Reservation createReservation(ReservationDTO dto, Passenger passenger, Bus bus) throws ReservationException {
        Reservation reservation = new Reservation();

        if (dto.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Journey Date should be in the future");
        }

        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDestination(dto.getDestination());
        reservation.setDate(dto.getJourneyDate());
        reservation.setStatus("Active");
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.now());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setBus(bus);
        reservation.setFare(bus.getFare() * dto.getBookedSeat());
        reservation.setBookedSeat(dto.getBookedSeat());

        reservation.setPassenger(passenger);

        return reservation;
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
            List<Reservation> reservations = passenger.getReservationList();

            return reservations.stream()
                    .filter(reservation -> {
                        String status = reservation.getStatus();
                        return status.equals("Active") || status.equals("Expired")|| status.equals("Cancelled");
                    })
                    .collect(Collectors.toList());
        } else {
            throw new ReservationException("You dont have any reservation at the moment.");
        }
    }



    private void updateReservationStatus(Reservation reservation, Bus bus) {
        if (bus != null && bus.getArrivalTime() != null && reservation.getJourneyDate() != null) {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            LocalDate arrivalDate = bus.getJourneyDate();
            LocalTime arrivalTime = bus.getArrivalTime();

            if (currentDate.isEqual(arrivalDate) && currentTime.isBefore(arrivalTime)) {
                reservation.setStatus("Active");
            } else if (currentDate.isEqual(arrivalDate) && currentTime.isAfter(arrivalTime)) {
                reservation.setStatus("Expired");
            }

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

        reservation.setStatus("Cancelled");
        reservationRepository.save(reservation);

        updateBusAvailability(reservation.getBus(), reservation.getBookedSeat(), true);

        scheduledReservationDeletion();

        return reservation;
    }




    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedRate = 24 * 60 * 60 * 1000)
    public void scheduledReservationDeletion() {
        LocalDate currentDate = LocalDate.now();

        List<Reservation> reservationsToDelete = reservationRepository
                .findByStatusAndJourneyDateBefore("Cancelled", currentDate.minusDays(1));

        reservationRepository.deleteAll(reservationsToDelete);
    }




    User getUserFromToken() throws ReservationException {
        Long passengerIdFromToken = requestAccessToken.getPassengerId();
        Passenger passenger = passengerRepository.findById(passengerIdFromToken)
                .orElseThrow(() -> new ReservationException("Passenger not found with the given ID: " + passengerIdFromToken));

        return passenger.getUser();
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



    private void updateBusAvailability(Bus bus, int bookedSeats, boolean increaseSeats) {
        int availableSeats = bus.getAvailableSeats();

        if (increaseSeats) {
            availableSeats += bookedSeats;
        } else {
            availableSeats -= bookedSeats;
        }

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
    public long countActiveReservationsForToday() {
        LocalDate currentDate = LocalDate.now();

        long activeReservationsCount = reservationRepository.countReservationsByDate(currentDate);

        long canceledReservationsCount = reservationRepository.countReservationsByDateAndStatus(currentDate, "Cancelled");

        return Math.max(activeReservationsCount - canceledReservationsCount, 0);
    }






}
