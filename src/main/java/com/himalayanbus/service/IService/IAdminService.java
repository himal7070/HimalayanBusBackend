package com.himalayanbus.service.IService;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;

public interface IAdminService {

    Admin createAdmin(Admin admin) throws AdminException;

    Admin updateAdmin(Admin admin, String key) throws AdminException;



}
