package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IPassengerService {


    @Transactional(rollbackFor = UserException.class)
    User addPassenger(User user) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User updatePassenger(Long userID, User updatedUser, Passenger updatedPassenger) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User deletePassenger(Long userID) throws UserException;

    @Transactional(readOnly = true)
    List<Passenger> viewAllPassengers() throws UserException;



}
