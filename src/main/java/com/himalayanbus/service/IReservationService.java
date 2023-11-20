package com.himalayanbus.service;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IReservationService {


    @Transactional
    Reservation addReservation(ReservationDTO dto, String jwtToken) throws ReservationException;

    @Transactional
    Reservation viewReservation(Integer rid, String jwtToken) throws ReservationException;

    @Transactional
    List<Reservation> getAllReservation(String jwtToken) throws ReservationException;

    @Transactional
    List<Reservation> viewReservationByUerId(Integer uid, String jwtToken) throws ReservationException;

    @Transactional
    Reservation deleteReservation(Integer rid, String jwtToken) throws ReservationException;

    @Transactional
    Reservation updateReservation(Integer rid, ReservationDTO dto, String jwtToken) throws ReservationException;
}
