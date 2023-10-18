package com.himalayanbus.service.IService;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.AdminLoginDTO;
import com.himalayanbus.persistence.entity.AdminLoginSession;

import javax.security.auth.login.LoginException;

public interface IAdminLoginService {

    AdminLoginSession adminLogin(AdminLoginDTO loginDTO) throws LoginException, AdminException;

    void adminLogout(String key) throws LoginException;

}
