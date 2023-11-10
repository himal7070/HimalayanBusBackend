package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IUserService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private static final String ROLE_ADMIN = "admin";

    public UserService(IUserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
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
    public User updateUser(User user, String jwtToken) throws UserException {
        validateUserAndRole(user, jwtToken);
        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User deleteUser(Integer userID, String jwtToken) throws UserException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);
        Integer userIdFromToken = (Integer) claims.get("sub");
        String userRoleFromToken = (String) claims.get("role");

        if (userIdFromToken.equals(userID) || ROLE_ADMIN.equals(userRoleFromToken)) {
            User user = getUserById(userID);
            userRepository.delete(user);
            return user;
        } else {
            throw new UserException("You don't have permission to delete this account.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> viewAllUsers(String jwtToken) throws UserException {
        validateAdminRole(jwtToken);
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserException("No users found!");
        }
        return userList;
    }

    private void validateUserAndRole(User user, String jwtToken) throws UserException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);
        Integer userIdFromToken = (Integer) claims.get("sub");
        String userRoleFromToken = (String) claims.get("role");

        Integer userIdFromUser = user.getUserID();

        if (userIdFromUser != null && !userIdFromUser.equals(userIdFromToken) && !ROLE_ADMIN.equals(userRoleFromToken)) {
            throw new UserException("Invalid user details, you can only update your own profile.");
        }
    }


    private void validateAdminRole(String jwtToken) throws UserException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        if (!ROLE_ADMIN.equals(claims.get("role"))) {
            throw new UserException("Only admin has permission to view all users.");
        }
    }

    private User getUserById(Integer userID) throws UserException {
        return userRepository.findById(userID).orElseThrow(() -> new UserException("Invalid user ID!"));
    }
}
