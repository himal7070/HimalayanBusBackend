package com.himalayanbus.service;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.User;
import org.springframework.transaction.annotation.Transactional;

public interface IAdminService {


    @Transactional
    User createAdmin(User admin) throws AdminException;

    @Transactional
    User updateAdmin(User admin, Long adminID) throws AdminException;


}
