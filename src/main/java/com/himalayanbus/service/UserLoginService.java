package com.himalayanbus.service;

import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserLoginDTO;
import com.himalayanbus.service.IService.IUserLoginService;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
public class UserLoginService implements IUserLoginService{

    private final IUserRepository iUserRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserLoginService(IUserRepository iUserRepository, JwtTokenUtil jwtTokenUtil) {
        this.iUserRepository = iUserRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public String userLogin(UserLoginDTO userLoginDTO) throws LoginException {
        User registeredUser = iUserRepository.findByEmail(userLoginDTO.getEmail());
        if (registeredUser == null) {
            throw new LoginException("Invalid email address.");
        }

        String userRole = registeredUser.getRole();

        return jwtTokenUtil.generateJwtToken(registeredUser.getUserID(), userRole);
    }




}
