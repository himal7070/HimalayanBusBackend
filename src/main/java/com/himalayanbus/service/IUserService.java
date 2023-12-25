package com.himalayanbus.service;

import com.himalayanbus.exception.UserException;
import com.himalayanbus.persistence.entity.User;
import org.springframework.transaction.annotation.Transactional;


public interface IUserService {
    @Transactional(readOnly = true)
    Object getUserInformationByEmail(String email) throws UserException;

    @Transactional(rollbackFor = UserException.class)
    User updatePasswordByEmail(String email, String oldPassword, String newPassword) throws UserException;

}
