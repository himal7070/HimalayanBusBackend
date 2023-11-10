package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.service.IService.IUserService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

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
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        Integer userIdFromToken = (Integer) claims.get("sub");
        String userRoleFromToken = (String) claims.get("role");

        Integer userIdFromUser = user.getUserID();

        if (userIdFromUser != null) {
            if (!userIdFromUser.equals(userIdFromToken) && !userRoleFromToken.equals("admin")) {
                throw new UserException("Invalid user details, you can only update your own profile.");
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional(rollbackFor = UserException.class)
    public User deleteUser(Integer userID, String jwtToken) throws UserException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        Integer userIdFromToken = (Integer) claims.get("sub");
        String userRoleFromToken = (String) claims.get("role");

        if (userIdFromToken.equals(userID) || userRoleFromToken.equals("admin")) {
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
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        if (claims.get("role").equals("admin")) {
            List<User> userList = userRepository.findAll();
            if (userList.isEmpty()) {
                throw new UserException("No users found!");
            }
            return userList;
        } else {
            throw new UserException("Only admin has permission to view all users.");
        }
    }

    private User getUserById(Integer userID) throws UserException {
        return userRepository.findById(userID).orElseThrow(() -> new UserException("Invalid user ID!"));
    }



    // "message": "The signing key's size is 112 bits which is not secure enough for the HS512 algorithm.  The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HS512 MUST have a size >= 512 bits (the key size must be greater than or equal to the hash output size).  Consider using the io.jsonwebtoken.security.Keys class's 'secretKeyFor(SignatureAlgorithm.HS512)' method to create a key guaranteed to be secure enough for HS512.  See https://tools.ietf.org/html/rfc7518#section-3.2 for more information.",
//         "details": "uri=/himalayanbus/admin/login"


}


