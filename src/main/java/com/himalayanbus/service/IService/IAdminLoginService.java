package com.himalayanbus.service.IService;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.AdminLoginDTO;

import javax.security.auth.login.LoginException;

public interface IAdminLoginService {

    String adminLogin(AdminLoginDTO loginDTO) throws LoginException, AdminException;

    void adminLogout(String jwtToken);

}
