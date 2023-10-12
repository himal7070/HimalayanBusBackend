package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.IRepository.IUserLoginSessionRepository;
import com.himalayanbus.persistence.IRepository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserLoginSession;
import com.himalayanbus.service.IService.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IUserLoginSessionRepository userLoginSessionRepository;

    public UserService(IUserRepository userRepository, IUserLoginSessionRepository userLoginSessionRepository) {
        this.userRepository = userRepository;
        this.userLoginSessionRepository = userLoginSessionRepository;
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User addUser(User user) throws UserException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("User is already registered!");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updateUser(User user, String key) throws UserException {
        UserLoginSession loggedInUser = getUserBySessionKey(key);
        checkValidUserForUpdate(loggedInUser, user);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User deleteUser(Integer userID, String key) throws UserException {
        UserLoginSession loggedInUser = getUserBySessionKey(key);
        User user = getUserById(userID);
        userRepository.delete(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> viewAllUsers(String key) throws UserException {
        UserLoginSession loggedInUser = getUserBySessionKey(key);
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserException("No users found!");
        }
        return userList;
    }






    //--------------------------------------  sub divided methods [lets say Dry principle]--------------------------------------

    private UserLoginSession getUserBySessionKey(String key) throws UserException {
        UserLoginSession loggedInUser = userLoginSessionRepository.findBySessionKey(key);
        if (loggedInUser == null) {
            throw new UserException("Please enter a valid key or login first!");
        }
        return loggedInUser;
    }

    private void checkValidUserForUpdate(UserLoginSession loggedInUser, User user) throws UserException {
        if (!Objects.equals(user.getUserID(), loggedInUser.getUserID())) {
            throw new UserException("Invalid user details, please login for updating user!");
        }
    }

    private User getUserById(Integer userID) throws UserException {
        return userRepository.findById(userID).orElseThrow(() -> new UserException("Invalid user ID!"));
    }
}


