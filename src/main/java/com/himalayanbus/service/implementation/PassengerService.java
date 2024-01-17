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

import java.util.List;
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



    @Override
    @Transactional(rollbackFor = UserException.class)
    public Passenger updatePassengerDetails(Long userID, Passenger updatedPassenger) throws UserException {
        // Retrieve the existing Passenger entity based on the provided userID
        Passenger existingPassenger = getPassengerByUserId(userID);

        if (existingPassenger != null && updatedPassenger != null) {
            // Update the existing Passenger details with the information from the updated Passenger
            existingPassenger.setFirstName(updatedPassenger.getFirstName());
            existingPassenger.setLastName(updatedPassenger.getLastName());
            existingPassenger.setPhoneNumber(updatedPassenger.getPhoneNumber());

            // Save the updated Passenger entity to the database
            return passengerRepository.save(existingPassenger);
        } else {
            // If either the existing Passenger or the updated Passenger details are null, throw an exception
            throw new UserException("Passenger details or passenger not found.");
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



    //--------------------------- Sub-divided methods [dark coder - aryal]----------------------------------


    public Passenger getPassengerByUserId(Long userID) {
        User user = userRepository.findById(userID).orElse(null);

        if (user != null) {
            return user.getPassenger();
        }

        return null;
    }

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


    private void deletePassengerDetails(User user, Passenger passenger) {
        user.setPassenger(null);
        userRepository.save(user);
        passengerRepository.delete(passenger);
    }

    private User getUserById(Long userID) throws UserException {
        return userRepository.findById(userID)
                .orElseThrow(() -> new UserException("Invalid user ID!"));
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








}
