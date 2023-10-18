package com.himalayanbus.service;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.IRepository.IAdminLoginSessionRepository;
import com.himalayanbus.persistence.IRepository.IAdminRepository;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.AdminLoginDTO;
import com.himalayanbus.persistence.entity.AdminLoginSession;
import com.himalayanbus.service.IService.IAdminLoginService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class AdminLoginService implements IAdminLoginService {

    private IAdminLoginSessionRepository iAdminLoginSessionRepository;

    private IAdminRepository iAdminRepository;

    @Override
    @Transactional(rollbackFor = {LoginException.class, AdminException.class})
    public AdminLoginSession adminLogin(AdminLoginDTO loginDTO) throws LoginException, AdminException {
        List<Admin> admins = iAdminRepository.findByEmail(loginDTO.getEmail());

        if (admins.isEmpty()) {
            throw new AdminException("Invalid email. Please provide a valid email.");
        }

        Admin registeredAdmin = admins.get(0);

        if (registeredAdmin == null) {
            throw new AdminException("Invalid email. Please provide a valid email.");
        }

        Optional<AdminLoginSession> loggedInAdmin = iAdminLoginSessionRepository.findById(registeredAdmin.getAdminID());
        if (loggedInAdmin.isPresent()) {
            throw new LoginException("Admin is already logged in.");
        }

        if (registeredAdmin.getPassword().equals(loginDTO.getPassword())) {
            String sessionKey = generateSessionKey();

            AdminLoginSession currentAdminSession = new AdminLoginSession();
            currentAdminSession.setAdminID(registeredAdmin.getAdminID());
            currentAdminSession.setSessionKey(sessionKey);
            currentAdminSession.setTime(LocalDateTime.now());
            return iAdminLoginSessionRepository.save(currentAdminSession);
        } else {
            throw new LoginException("Invalid password. Please enter a valid password.");
        }
    }

    @Override
    @Transactional(rollbackFor = {LoginException.class, AdminException.class})
    public void adminLogout(String key) throws LoginException {
        AdminLoginSession currentAdminSession = iAdminLoginSessionRepository.findBySessionKey(key);
        if (currentAdminSession == null) {
            throw new LoginException("Invalid Admin login key.");
        }
        iAdminLoginSessionRepository.delete(currentAdminSession);
    }



    // -------------------------------------- session key generator method --------------------------------------

    private String generateSessionKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[10];
        secureRandom.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }



}
