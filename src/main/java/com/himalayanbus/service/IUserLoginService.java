package com.himalayanbus.service;

import com.himalayanbus.persistence.entity.UserLoginDTO;

import javax.security.auth.login.LoginException;

public interface IUserLoginService {

    String userLogin(UserLoginDTO userLoginDTO) throws LoginException;



}
