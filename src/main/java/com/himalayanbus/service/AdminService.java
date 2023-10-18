package com.himalayanbus.service;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.IRepository.IAdminLoginSessionRepository;
import com.himalayanbus.persistence.IRepository.IAdminRepository;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.AdminLoginSession;
import com.himalayanbus.service.IService.IAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
public class AdminService implements IAdminService {

    private final IAdminRepository iAdminRepository;

    private final IAdminLoginSessionRepository iAdminSessionRepository;


    public AdminService(IAdminRepository iAdminRepository, IAdminLoginSessionRepository iAdminSessionRepository) {
        this.iAdminRepository = iAdminRepository;
        this.iAdminSessionRepository = iAdminSessionRepository;
    }


    @Override
    @Transactional
    public Admin createAdmin(Admin admin) throws AdminException {
        List<Admin> existingAdmins = iAdminRepository.findByEmail(admin.getEmail());

        if (!existingAdmins.isEmpty()) {
            throw new AdminException("An admin with the email " + admin.getEmail() + " already exists.");
        }

        return iAdminRepository.save(admin);
    }

    @Override
    @Transactional
    public Admin updateAdmin(Admin admin, String sessionKey) throws AdminException {
        AdminLoginSession adminSession = iAdminSessionRepository.findBySessionKey(sessionKey);
        if (adminSession == null) {
            throw new AdminException("Invalid session key or not logged in. Please log in first.");
        }

        if (!Objects.equals(admin.getAdminID(), adminSession.getAdminID())) {
            throw new AdminException("Invalid admin details. You must log in to update the admin information.");
        }

        return iAdminRepository.save(admin);
    }

}
