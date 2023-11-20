package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserService {


    @Transactional(rollbackFor = UserException.class)
    User addUser(User user) throws UserException;


    @Transactional(rollbackFor = UserException.class)
    User updateUser(Integer userID, User updatedUser) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User deleteUser(Integer userID) throws UserException;

    @Transactional(readOnly = true)
    List<User> viewAllUsers() throws UserException;


}
