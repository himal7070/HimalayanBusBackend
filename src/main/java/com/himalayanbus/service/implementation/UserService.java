package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    public UserService(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    @Override
    @Transactional(rollbackFor = UserException.class)
    public User addUser(User user) throws UserException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserException("User is already registered!");
        }

        Role userRole = roleRepository.findByRole(UserRole.USER);

        if (userRole == null) {
            userRole = new Role();
            userRole.setRole(UserRole.USER);
            userRole = roleRepository.save(userRole);
        }

        user.getRoles().add(userRole);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User updateUser(Integer userID, User updatedUser) throws UserException {
        User existingUser = getUserById(userID);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setPassword(updatedUser.getPassword());


        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User deleteUser(Integer userID) throws UserException {
        User user = getUserById(userID);
        userRepository.delete(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)

    public List<User> viewAllUsers() throws UserException {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserException("No users found!");
        }
        return userList;
    }







    //--------------------------------------  sub divided method --------------------------------------

    private User getUserById(Integer userID) throws UserException {
        return userRepository.findById(userID).orElseThrow(() -> new UserException("Invalid user ID!"));
    }


}
