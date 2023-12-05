package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;


@Repository
public interface IReservationRepository extends JpaRepository <Reservation, Long> {

    long countReservationsByDate(LocalDate date);

}
