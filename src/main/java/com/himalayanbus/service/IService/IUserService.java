package com.himalayanbus.service.IService;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;

import java.util.List;

public interface IUserService {

    User addUser(User user) throws UserException;

    User updateUser(User user, String jwtToken) throws UserException;

    User deleteUser(Integer userID, String jwtToken) throws UserException;

    List<User> viewAllUsers(String jwtToken) throws UserException;


}
