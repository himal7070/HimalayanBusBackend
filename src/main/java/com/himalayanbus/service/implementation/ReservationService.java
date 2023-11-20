package com.himalayanbus.service.implementation;


import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IReservationRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
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


    public ReservationService(IReservationRepository reservationRepository, IBusRepository busRepository, IUserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public Reservation addReservation(ReservationDTO dto, String jwtToken) throws ReservationException {
        Integer userId = extractUserIdFromToken(jwtToken);
        User user = getUserFromToken(userId);

        Bus bus = findBusByLocations(dto.getDepartureLocation(), dto.getDestination());

        Integer availableSeats = bus.getAvailableSeats();

        if (availableSeats < dto.getBookedSeat()) throw new ReservationException("Only " + availableSeats + " seats are available");

        availableSeats -= dto.getBookedSeat();
        bus.setAvailableSeats(availableSeats);

        Reservation reservation = createReservation(dto, user, bus);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Integer rid, ReservationDTO dto, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Bus bus = findBusByLocations(dto.getDepartureLocation(), dto.getDestination());

        Optional<Reservation> optional = reservationRepository.findById(rid);

        if (optional.isEmpty()) throw new ReservationException("Reservation not found with the given id: " + rid);

        Reservation reservation = optional.get();

        Integer availableSeats = bus.getAvailableSeats();

        if (availableSeats < dto.getBookedSeat()) throw new ReservationException("Only " + availableSeats + " seats are available");

        availableSeats -= dto.getBookedSeat();
        bus.setAvailableSeats(availableSeats);

        updateReservationDetails(dto, bus, reservation);

        return reservationRepository.save(reservation);
    }


    private Bus findBusByLocations(String departureLocation, String destination) throws ReservationException {
        Bus bus = busRepository.findByRouteFromAndRouteTo(departureLocation, destination);

        if (bus == null) throw new ReservationException("Bus not found for the given starting to destination");

        return bus;
    }


    // Utility method to get user from token
    private User getUserFromToken(Integer userId) throws ReservationException {
        Optional<User> optional = userRepository.findById(userId);

        if (optional.isEmpty()) throw new ReservationException("User not found with the provided token");

        return optional.get();
    }

    // Utility method to create a new Reservation
    private Reservation createReservation(ReservationDTO dto, User user, Bus bus) throws ReservationException {
        Reservation reservation = new Reservation();

        if (dto.getJourneyDate().isBefore(LocalDate.now())) throw new ReservationException("Journey Date should be in the future");

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

        return reservation;
    }

    // Utility method to update reservation details
    private void updateReservationDetails(ReservationDTO dto, Bus bus, Reservation reservation) {
        reservation.setBookedSeat(dto.getBookedSeat());
        reservation.setBus(bus);
        reservation.setDate(dto.getJourneyDate());
        reservation.setDestination(dto.getDestination());
        reservation.setFare(bus.getFare());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.now());
    }





    @Override
    @Transactional
    public Reservation viewReservation(Integer rid, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<Reservation> optional = reservationRepository.findById(rid);

        if(optional.isEmpty()) throw new ReservationException("Reservation with given id is not found");

        return optional.get();
    }

    @Override
    @Transactional
    public List<Reservation> getAllReservation(String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        List<Reservation> list = reservationRepository.findAll();

        if(list.isEmpty()) throw new ReservationException("Reservation Not found");

        return list;
    }

    @Override
    @Transactional
    public List<Reservation> viewReservationByUerId(Integer uid, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<User> optional = userRepository.findById(uid);

        if(optional.isEmpty()) throw new ReservationException("User not find with given user id: " + uid);

        User user = optional.get();

        List<Reservation> reservations = user.getReservationList();

        if(reservations.isEmpty()) throw new ReservationException("Reservation not found for this user");

        return reservations;
    }

    @Override
    @Transactional
    public Reservation deleteReservation(Integer rid, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<Reservation> optional =  reservationRepository.findById(rid);

        if(optional.isEmpty()) throw new ReservationException("Reservation not found with the given id: " + rid);

        Reservation reservation = optional.get();

        if(reservation.getJourneyDate().isBefore(LocalDate.now())) throw new ReservationException("Reservation Already Expired");

        Integer n = reservation.getBus().getAvailableSeats();

        reservation.getBus().setAvailableSeats(n + reservation.getBookedSeat());

        Bus bus = reservation.getBus();

        busRepository.save(bus);
        reservationRepository.delete(reservation);

        return reservation;
    }



    private void authenticateUser(String jwtToken) throws ReservationException {
        Integer userId = extractUserIdFromToken(jwtToken);
        Optional<User> optional = userRepository.findById(userId);

        if(optional.isEmpty()) throw new ReservationException("User not found with the provided token");
    }


    private Integer extractUserIdFromToken(String jwtToken) {
        // Logic to extract and decode JWT token to get user ID
        // This implementation might involve using libraries like jjwt or Spring Security's JwtDecoder
        // For example:
        // Jwt jwt = jwtDecoder.decode(jwtToken);
        // return jwt.getClaim("userId").asInteger();
        return 1; // Placeholder, replace with actual logic to extract user ID
    }
















}
