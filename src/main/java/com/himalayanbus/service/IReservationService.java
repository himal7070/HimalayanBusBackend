package com.himalayanbus.service;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IReservationService {


    @Transactional
    Reservation addReservation(ReservationDTO dto) throws ReservationException;


    @Transactional
    Reservation updateReservation(Long reservationId, ReservationDTO dto) throws ReservationException;

    @Transactional
    Reservation viewReservation(Long reservationId) throws ReservationException;

    @Transactional
    List<Reservation> getAllReservation() throws ReservationException;


    @Transactional
    List<Reservation> viewReservationsForCurrentUser() throws ReservationException;

    @Transactional
    Reservation deleteReservation(Long reservationId) throws ReservationException;

    @Transactional
    List<Reservation> viewReservationByUserId(Long uid) throws ReservationException;

    @Transactional(readOnly = true)
    long countActiveReservationsForToday() throws ReservationException;

}
