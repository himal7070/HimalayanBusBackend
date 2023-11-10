package com.himalayanbus.service;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import org.springframework.transaction.annotation.Transactional;

public interface IAdminService {

    @Transactional
    Admin createAdmin(Admin admin) throws AdminException;

    @Transactional
    Admin updateAdmin(Admin admin, String jwtToken) throws AdminException;


}
