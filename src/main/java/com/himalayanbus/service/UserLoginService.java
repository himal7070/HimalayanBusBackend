package com.himalayanbus.service;

import com.himalayanbus.persistence.IRepository.IUserLoginSessionRepository;
import com.himalayanbus.persistence.IRepository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserLoginDTO;
import com.himalayanbus.persistence.entity.UserLoginSession;
import com.himalayanbus.service.IService.IUserLoginService;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserLoginService implements IUserLoginService{

    private final IUserRepository iUserRepository ;
    private final IUserLoginSessionRepository iUserLoginSessionRepository;


    public UserLoginService(IUserRepository iUserRepository, IUserLoginSessionRepository iUserLoginSessionRepository) {
        this.iUserRepository = iUserRepository;
        this.iUserLoginSessionRepository = iUserLoginSessionRepository;
    }

    @Override
    public UserLoginSession userLogin(UserLoginDTO userLoginDTO) throws LoginException {
        User registeredUser = iUserRepository.findByEmail(userLoginDTO.getEmail());
        if (registeredUser == null) {
            throw new LoginException("Invalid email address.");
        }

        Optional<UserLoginSession> loggedInUser = iUserLoginSessionRepository.findById(registeredUser.getUserID());
        if (loggedInUser.isPresent()) {
            throw new LoginException("User is already logged in.");
        }

        String key = generateSessionKey();
        UserLoginSession currentUserLoginSession = new UserLoginSession();
        currentUserLoginSession.setUserID(registeredUser.getUserID());
        currentUserLoginSession.setSessionKey(key);
        currentUserLoginSession.setTime(LocalDateTime.now());
        return iUserLoginSessionRepository.save(currentUserLoginSession);
    }


    @Override
    public String userLogout(String key) throws LoginException {
        UserLoginSession loggedInUser = iUserLoginSessionRepository.findBySessionKey(key);
        if (loggedInUser == null) {
            throw new LoginException("Invalid key or user not logged in.");
        }

        iUserLoginSessionRepository.delete(loggedInUser);
        return "User logged out successfully.";
    }






//-------------------------------------- Session key generator method --------------------------------------

    private String generateSessionKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[10];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }



}
