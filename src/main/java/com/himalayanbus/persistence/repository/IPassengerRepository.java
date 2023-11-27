package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IPassengerRepository extends JpaRepository<Passenger,Long> {


}
