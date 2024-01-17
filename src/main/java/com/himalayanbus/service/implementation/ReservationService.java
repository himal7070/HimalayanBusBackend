package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.*;
import com.himalayanbus.persistence.repository.*;
import com.himalayanbus.security.token.AccessToken;
import com.himalayanbus.service.IReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IBusRepository busRepository;
    private final IRouteRepository routeRepository;

    private final IUserRepository userRepository;
    private final AccessToken requestAccessToken;
    private static final String STATUS_CANCELLED = "Cancelled";
    private static final String STATUS_ACTIVE = "Active";

    public ReservationService(IReservationRepository reservationRepository, IBusRepository busRepository
            , IRouteRepository routeRepository, IUserRepository userRepository,
                              AccessToken requestAccessToken) {
        this.reservationRepository = reservationRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
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
        updateReservationStatus();

        return reservationRepository.save(reservation);
    }


    @Scheduled(fixedRate = 60000)
    public void updateReservationStatus() {
        List<Reservation> reservations = reservationRepository.findAll();

        for (Reservation reservation : reservations) {
            updateStatusBasedOnTime(reservation);
        }
    }

    private void updateStatusBasedOnTime(Reservation reservation) {
        if (reservation.getBus() == null || reservation.getBus().getArrivalTime() == null || reservation.getJourneyDate() == null) {
            return;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime arrivalDateTime = LocalDateTime.of(reservation.getBus().getJourneyDate(), LocalTime.from(reservation.getBus().getArrivalTime()));

        if (currentDateTime.isEqual(arrivalDateTime) || currentDateTime.isAfter(arrivalDateTime)) {
            updateStatus(reservation, "Expired");
        } else {
            updateStatus(reservation, STATUS_ACTIVE);
        }
    }

    private void updateStatus(Reservation reservation, String newStatus) {
        if (reservation.getStatus() == null || !reservation.getStatus().equals(STATUS_CANCELLED)) {
            reservation.setStatus(newStatus);
            reservationRepository.save(reservation);
        }
    }




    Reservation createReservation(ReservationDTO dto, Passenger passenger, Bus bus) throws ReservationException {
        Reservation reservation = new Reservation();

        if (dto.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Journey Date should be in the future");
        }

        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDestination(dto.getDestination());
        reservation.setDate(dto.getJourneyDate());
        reservation.setStatus(STATUS_ACTIVE);
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.now());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setBus(bus);
        reservation.setFare(bus.getFare() * dto.getBookedSeat());
        reservation.setBookedSeat(dto.getBookedSeat());

        reservation.setPassenger(passenger);

        return reservation;
    }


    User getUserFromToken() throws ReservationException {
        Long userIdFromToken = requestAccessToken.getUserID();

        User user = userRepository.findById(userIdFromToken)
                .orElseThrow(() -> new ReservationException("User not found with the given ID: " + userIdFromToken));

        Passenger passenger = user.getPassenger();

        if (passenger == null) {
            throw new ReservationException("Passenger not found for the given user ID: " + userIdFromToken);
        }

        return user;
    }




    @Override
    @Transactional
    public Reservation deleteReservation(Long rid) throws ReservationException {
        User user = getUserFromToken();
        Reservation reservation = getReservationForUser(user, rid);

        LocalDate currentDate = LocalDate.now();

        validateReservationDeletion(rid, currentDate);

        reservation.setStatus(STATUS_CANCELLED);
        reservationRepository.save(reservation);

        if (reservation.getBookedSeat() != null) {
            updateBusAvailability(reservation.getBus(), reservation.getBookedSeat(), true);
        } else {
            throw new ReservationException("Booked seats information not found for the reservation");
        }

        scheduledReservationDeletion();

        return reservation;
    }


    @Scheduled(initialDelay = 24 * 60 * 60 * 1000, fixedRate = 24 * 60 * 60 * 1000)
    public void scheduledReservationDeletion() {
        System.out.println("Scheduled deletion logic started!");
        LocalDate currentDate = LocalDate.now();

        List<Reservation> reservationsToDelete = reservationRepository
                .findByStatusAndDateBefore(STATUS_CANCELLED, currentDate.minusDays(1));

        reservationsToDelete.forEach(reservation ->
                System.out.println("Reservation to delete: " + reservation.getReservationID())
        );

        reservationRepository.deleteAll(reservationsToDelete);
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
        User currentUser = getUserFromToken();

        Passenger passenger = currentUser.getPassenger();
        if (passenger != null) {
            List<Reservation> reservations = passenger.getReservationList();

            return reservations.stream()
                    .filter(reservation -> {
                        String status = reservation.getStatus();
                        return status.equals(STATUS_ACTIVE) || status.equals("Expired") || status.equals(STATUS_CANCELLED);
                    })
                    .toList();
        } else {
            // Modify the exception message to provide more clarity
            throw new ReservationException("Passenger details not found for the current user.");
        }
    }



    Route findRoute(String departureLocation, String destination) throws ReservationException {
        Route route = routeRepository.findByRouteFromAndRouteTo(departureLocation, destination);

        if (route == null) {
            throw new ReservationException("Route not found for the given starting to destination");
        }

        return route;
    }


    void updateBusAvailability(Bus bus, int bookedSeats, boolean increaseSeats) {
        int availableSeats = bus.getAvailableSeats();

        if (increaseSeats) {
            availableSeats += bookedSeats;
        } else {
            availableSeats -= bookedSeats;
        }

        bus.setAvailableSeats(availableSeats);
    }


    Reservation getReservationForUser(User user, Long reservationId) throws ReservationException {

        if (user == null) {
            throw new ReservationException("User details not provided");
        }

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


    void validateReservationDeletion(Long reservationId, LocalDate currentDate) throws ReservationException {
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

        long canceledReservationsCount = reservationRepository.countReservationsByDateAndStatus(currentDate, STATUS_CANCELLED);

        return Math.max(activeReservationsCount - canceledReservationsCount, 0);
    }


    @Override
    @Transactional
    public List<Reservation> getAllReservationsByBusId(Long busId) throws ReservationException {
        List<Reservation> reservations = reservationRepository.findAllByBusBusId(busId);

        if (reservations.isEmpty()) {
            throw new ReservationException("No reservations found for the bus with ID: " + busId);
        }

        return reservations;
    }

}