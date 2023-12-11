package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Passenger;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IPassengerRepository;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.service.IPassengerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class PassengerService implements IPassengerService {

    private final IUserRepository userRepository;
    private final IPassengerRepository passengerRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public PassengerService(IUserRepository userRepository, IPassengerRepository passengerRepository,
                            IRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional(rollbackFor = UserException.class)
    public User addPassenger(User user) throws UserException {
        validateNewUser(user);

        Role userRole = getOrCreateUserRole();

        user.getRoles().add(userRole);
        user.setPassword(hashPassword(user.getPassword()));

        User savedUser = userRepository.save(user);

        createOrUpdatePassengerDetails(user, savedUser);

        return savedUser;
    }


    public Passenger getPassengerById(Long passengerID) {
        return passengerRepository.findById(passengerID).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public Passenger updatePassengerDetails(Long passengerID, Passenger updatedPassenger) throws UserException {
        Passenger existingPassenger = getPassengerById(passengerID);

        if (existingPassenger != null && updatedPassenger != null) {

            return passengerRepository.save(existingPassenger);
        } else {
            throw new UserException("Passenger details or passenger not found.");
        }
    }


    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updatePasswordForPassenger(Long passengerID, String newPassword) throws UserException {
        Passenger existingPassenger = getPassengerById(passengerID);

        if (existingPassenger != null) {
            User user = existingPassenger.getUser();

            if (user != null) {
                if (newPassword != null && !newPassword.isEmpty()) {
                    user.setPassword(newPassword);
                } else {
                    throw new UserException("New password cannot be empty.");
                }

                return userRepository.save(user);
            } else {
                throw new UserException("User not found for the provided passenger.");
            }
        } else {
            throw new UserException("Passenger not found with the provided ID.");
        }
    }







    @Override
    @Transactional(rollbackFor = UserException.class)
    public User deletePassenger(Long userID) throws UserException {
        User user = getUserById(userID);
        Passenger passenger = user.getPassenger();

        if (passenger != null) {
            deletePassengerDetails(user, passenger);
        }

        userRepository.delete(user);
        return user;
    }


    @Override
    @Transactional(readOnly = true)
    public void viewAllPassengersWithUserDetails() throws UserException {
        List<Object[]> passengerList = passengerRepository.findAllPassengersWithUserDetails();

        if (passengerList.isEmpty()) {
            throw new UserException("No passengers found!");
        }

    }


    @Override
    @Transactional(readOnly = true)
    public long getTotalPassengerCount() throws UserException {
        long count = passengerRepository.count();

        if (count == 0) {
            throw new UserException("No passengers available at the moment");
        }

        return count;
    }


    @Override
    @Transactional(readOnly = true)
    public Object getUserInformationByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            Map<String, Object> userInformation = new HashMap<>();
            if (user.getImageProfileUrl() != null) {
                userInformation.put("imageProfileUrl", user.getImageProfileUrl());
            }

            if (user.getPassenger() != null) {
                Passenger passenger = user.getPassenger();
                userInformation.put("passengerDetails", passenger);
                if (passenger.getFirstName() != null && passenger.getLastName() != null) {
                    userInformation.put("firstName", passenger.getFirstName());
                    userInformation.put("lastName", passenger.getLastName());
                    userInformation.put("phoneNumber", passenger.getPhoneNumber());
                } else {
                    userInformation.put("email", user.getEmail());
                }
            } else {
                userInformation.put("userDetails", user);
                userInformation.put("email", user.getEmail());
            }

            return userInformation;
        } else {
            throw new UserException("User not found with this email!");
        }

    }






    //--------------------------- Sub-divided methods [dark coder - aryal]----------------------------------

    private void validateNewUser(User user) throws UserException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("User is already registered!");
        }

        if (user.getPassenger() == null) {
            throw new UserException("Passenger details are required!");
        }
    }

    private Role getOrCreateUserRole() {
        Role userRole = roleRepository.findByRole(UserRole.USER);

        if (userRole == null) {
            userRole = new Role();
            userRole.setRole(UserRole.USER);
            userRole = roleRepository.save(userRole);
        }

        return userRole;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void createOrUpdatePassengerDetails(User user, User savedUser) {
        Optional.ofNullable(user.getPassenger()).ifPresent(passenger -> {
            Passenger passengerDetails = new Passenger();
            passengerDetails.setFirstName(passenger.getFirstName());
            passengerDetails.setLastName(passenger.getLastName());
            passengerDetails.setPhoneNumber(passenger.getPhoneNumber());

            passengerDetails.setUser(savedUser);

            passengerDetails = passengerRepository.save(passengerDetails);

            savedUser.setPassenger(passengerDetails);
            userRepository.save(savedUser);
        });
    }


    private void updatePasswordIfNotEmpty(User existingUser, String updatedPassword) {
        if (!updatedPassword.isEmpty()) {
            String hashedPassword = passwordEncoder.encode(updatedPassword);
            existingUser.setPassword(hashedPassword);
        }
    }

    private void updatePassengerDetailsIfExists(User existingUser, Passenger updatedPassenger) {
        Passenger existingPassenger = existingUser.getPassenger();
        if (existingPassenger != null && updatedPassenger != null) {
            existingPassenger.setFirstName(updatedPassenger.getFirstName());
            existingPassenger.setLastName(updatedPassenger.getLastName());
            existingPassenger.setPhoneNumber(updatedPassenger.getPhoneNumber());

            passengerRepository.save(existingPassenger);
        }
    }

    private void deletePassengerDetails(User user, Passenger passenger) {
        user.setPassenger(null);
        userRepository.save(user);
        passengerRepository.delete(passenger);
    }

    private User getUserById(Long userID) throws UserException {
        return userRepository.findById(userID)
                .orElseThrow(() -> new UserException("Invalid user ID!"));
    }






}
