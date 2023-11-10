package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IReservationRepository extends JpaRepository <Reservation, Integer> {

}
