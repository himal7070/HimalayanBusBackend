package com.himalayanbus.service.IService;

import com.himalayanbus.exception.ReservationException;
import com.himalayanbus.persistence.entity.Reservation;
import com.himalayanbus.persistence.entity.ReservationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IReservationService {

    @Transactional
    Reservation addReservation(ReservationDTO dto, String jwtToken) throws ReservationException;

    @Transactional
    Reservation updateReservation(Integer rid, ReservationDTO dto, String jwtToken) throws ReservationException;

    @Transactional
    Reservation deleteReservation(Integer rid, String jwtToken) throws ReservationException;

    @Transactional(readOnly = true)
    Reservation viewReservationByRID(Integer reservationID, String jwtToken) throws ReservationException;

    @Transactional(readOnly = true)
    List<Reservation> getAllReservation(String jwtToken) throws ReservationException;

    @Transactional(readOnly = true)
    List<Reservation> viewReservationByUserId(Integer userID, String jwtToken) throws ReservationException;
}
