package com.himalayanbus.service.IService;

import com.himalayanbus.persistence.entity.UserLoginDTO;
import com.himalayanbus.persistence.entity.UserLoginSession;

import javax.security.auth.login.LoginException;

public interface IUserLoginService {

    UserLoginSession userLogin(UserLoginDTO userLoginDTO) throws LoginException;
    String userLogout(String key) throws LoginException;

    
}
