package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserInformationByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            Map<String, Object> userInformation = new HashMap<>();
            userInformation.put("email", user.getEmail());
            if (user.getImageProfileUrl() != null) {
                userInformation.put("imageProfileUrl", user.getImageProfileUrl());
            }

            if (user.getPassenger() != null) {
                Passenger passenger = user.getPassenger();
                userInformation.put("firstName", passenger.getFirstName());
                userInformation.put("lastName", passenger.getLastName());
                userInformation.put("phoneNumber", passenger.getPhoneNumber());

            }

            return userInformation;
        } else {
            throw new UserException("User not found with this email!");
        }

    }



    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updatePasswordByEmail(String email, String oldPassword, String newPassword) throws UserException {

        User existingUser = userRepository.findByEmail(email);

        if (existingUser != null) {
            if (newPassword != null && !newPassword.isEmpty()) {
                if (passwordEncoder.matches(oldPassword, existingUser.getPassword())) {
                    String hashedNewPassword = passwordEncoder.encode(newPassword);
                    existingUser.setPassword(hashedNewPassword);
                    return userRepository.save(existingUser);
                } else {
                    throw new UserException("Old password is incorrect.");
                }
            } else {
                throw new UserException("New password cannot be empty.");
            }
        } else {
            throw new UserException("User not found for the provided email.");
        }

    }



}
