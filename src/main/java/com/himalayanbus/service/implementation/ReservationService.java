package com.himalayanbus.service.implementation;


import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.repository.IBusRepository;
import com.himalayanbus.persistence.repository.IReservationRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IReservationService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService implements IReservationService {


    private final IReservationRepository iReservationRepository;
    private final IBusRepository iBusRepository;
    private final IUserRepository iUserRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public ReservationService(IReservationRepository iReservationRepository, IBusRepository iBusRepository, IUserRepository iUserRepository, JwtTokenUtil jwtTokenUtil) {
        this.iReservationRepository = iReservationRepository;
        this.iBusRepository = iBusRepository;
        this.iUserRepository = iUserRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    private Bus findBusByRoute(String departureLocation, String destination) {
        return iBusRepository.findByRouteFromAndRouteTo(departureLocation, destination);
    }

    @Transactional
    @Override
    public Reservation addReservation(ReservationDTO dto, String jwtToken) throws ReservationException {
        User user = authenticateUser(jwtToken);
        Bus bus = findBusByRoute(dto.getDepartureLocation(), dto.getDestination());
        updateAvailableSeats(bus, dto.getBookedSeat());

        Reservation reservation = new Reservation();

        if (dto.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Journey date should be in the future");
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

        return iReservationRepository.save(reservation);
    }

    @Transactional
    @Override
    public Reservation updateReservation(Integer rid, ReservationDTO dto, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);
        Bus bus = findBusByRoute(dto.getDepartureLocation(), dto.getDestination());
        updateAvailableSeats(bus, dto.getBookedSeat());

        Optional<Reservation> optionalReservation = iReservationRepository.findById(rid);

        if (optionalReservation.isEmpty()) {
            throw new ReservationException("Reservation not found with the given id: " + rid);
        }

        Reservation reservation = optionalReservation.get();
        reservation.setBookedSeat(dto.getBookedSeat());
        reservation.setBus(bus);
        reservation.setDate(dto.getJourneyDate());
        reservation.setDestination(dto.getDestination());
        reservation.setFare(bus.getFare());
        reservation.setJourneyDate(dto.getJourneyDate());
        reservation.setDepartureLocation(dto.getDepartureLocation());
        reservation.setDate(LocalDate.now());
        reservation.setTime(LocalTime.now());

        iReservationRepository.save(reservation);

        return reservation;
    }

    @Transactional
    @Override
    public Reservation deleteReservation(Integer rid, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<Reservation> optionalReservation = iReservationRepository.findById(rid);

        if (optionalReservation.isEmpty()) {
            throw new ReservationException("Reservation not found with the given id: " + rid);
        }

        Reservation reservation = optionalReservation.get();

        if (reservation.getJourneyDate().isBefore(LocalDate.now())) {
            throw new ReservationException("Reservation has already expired");
        }

        updateAvailableSeats(reservation.getBus(), reservation.getBookedSeat());

        iReservationRepository.delete(reservation);

        return reservation;
    }

    @Transactional(readOnly = true)
    @Override
    public Reservation viewReservationByRID(Integer reservationID, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<Reservation> optionalReservation = iReservationRepository.findById(reservationID);

        if (optionalReservation.isEmpty()) {
            throw new ReservationException("Reservation with given id is not found");
        }

        return optionalReservation.get();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Reservation> getAllReservation(String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        List<Reservation> list = iReservationRepository.findAll();

        if (list.isEmpty()) {
            throw new ReservationException("No reservations found");
        }

        return list;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Reservation> viewReservationByUserId(Integer userID, String jwtToken) throws ReservationException {
        authenticateUser(jwtToken);

        Optional<User> optionalUser = iUserRepository.findById(userID);

        if (optionalUser.isEmpty()) {
            throw new ReservationException("User not found with the given user ID: " + userID);
        }

        User requestedUser = optionalUser.get();
        List<Reservation> reservations = requestedUser.getReservationList();

        if (reservations.isEmpty()) {
            throw new ReservationException("No reservations found for this user");
        }

        return reservations;
    }





    private void updateAvailableSeats(Bus bus, int bookedSeats) throws ReservationException {
        int availableSeats = bus.getAvailableSeats();
        if (availableSeats < bookedSeats) {
            throw new ReservationException("Insufficient available seats");
        }
        availableSeats -= bookedSeats;
        bus.setAvailableSeats(availableSeats);
        iBusRepository.save(bus);
    }





    // -------------------------- Helper method to authenticate a user using JWT --------------------------


    private User authenticateUser(String jwtToken) throws ReservationException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        Integer userId = (Integer) claims.get("sub");

        if (userId == null) {
            throw new ReservationException("Invalid or missing user ID in the token.");
        }

        Optional<User> optionalUser = iUserRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new ReservationException("User not found with the provided token.");
        }

        return optionalUser.get();
    }






}
