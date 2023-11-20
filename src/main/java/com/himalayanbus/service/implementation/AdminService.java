package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IAdminRepository;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.service.IAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AdminService implements IAdminService {

    private final IAdminRepository adminRepository;

    private final IRoleRepository roleRepository;
    public AdminService(IAdminRepository iAdminRepository, IRoleRepository roleRepository) {
        this.adminRepository = iAdminRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    @Transactional
    public Admin createAdmin(Admin admin) throws AdminException {
        Admin existingAdmin = adminRepository.findByEmail(admin.getEmail());

        if (existingAdmin != null) {
            throw new AdminException("An admin with the email " + admin.getEmail() + " already exists.");
        }

        Role adminRole = roleRepository.findByRole(UserRole.ADMIN);

        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setRole(UserRole.ADMIN);
            adminRole = roleRepository.save(adminRole);
        }

        admin.getRoles().add(adminRole);
        return adminRepository.save(admin);
    }

    @Override
    @Transactional
    public Admin updateAdmin(Admin admin, Integer adminID) throws AdminException {
        Admin existingAdmin = adminRepository.findById(adminID)
                .orElseThrow(() -> new AdminException("Admin not found with ID: " + adminID));

        existingAdmin.setUserName(admin.getUserName());
        existingAdmin.setEmail(admin.getEmail());
        existingAdmin.setPassword(admin.getPassword());

        return adminRepository.save(existingAdmin);

    }



}
