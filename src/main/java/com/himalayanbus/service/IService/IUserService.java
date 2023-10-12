package com.himalayanbus.service.IService;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;

import java.util.List;

public interface IUserService {

    User addUser(User user) throws UserException;

    User updateUser(User user, String sessionID) throws UserException;

    User deleteUser(Integer userId, String sessionID) throws UserException;

    List<User> viewAllUsers(String key) throws UserException;


}
