package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IPassengerRepository extends JpaRepository<Passenger,Long> {
    @Query("SELECT p.passengerId, p.firstName, p.lastName, p.phoneNumber, u.email, COUNT(r) " +
            "FROM Passenger p " +
            "JOIN p.user u " +
            "LEFT JOIN p.reservationList r " +
            "GROUP BY p.passengerId, u.email")
    List<Object[]>  findAllPassengersWithUserDetails();



}
