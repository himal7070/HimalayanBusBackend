package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface IPassengerService {


    @Transactional(rollbackFor = UserException.class)
    User addPassenger(User user) throws UserException;


    @Transactional(rollbackFor = UserException.class)
    Passenger updatePassengerDetails(Long passengerID, Passenger updatedPassenger) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User updatePasswordForPassenger(Long passengerID, String newPassword) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User deletePassenger(Long userID) throws UserException;

    @Transactional(readOnly = true)
    void viewAllPassengersWithUserDetails() throws UserException;


    @Transactional(readOnly = true)
    long getTotalPassengerCount() throws UserException;


    @Transactional(readOnly = true)
    Object getUserInformationByEmail(String email) throws UserException;
}
