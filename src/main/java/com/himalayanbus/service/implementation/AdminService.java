package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.User;
import com.himalayanbus.persistence.entity.UserRole;
import com.himalayanbus.persistence.repository.IRoleRepository;
import com.himalayanbus.persistence.repository.IUserRepository;
import com.himalayanbus.service.IAdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AdminService implements IAdminService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleRepository roleRepository;

    private static final String ADMIN_ALREADY_EXISTS = "An admin with the email %s already exists.";
    private static final String ADMIN_NOT_FOUND = "Admin not found with ID: %d";

    public AdminService(IUserRepository userRepository, PasswordEncoder passwordEncoder, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User createAdmin(User admin) throws AdminException {
        validateAdminEmail(admin.getEmail());

        Role adminRole = getOrCreateAdminRole();

        admin.getRoles().add(adminRole);
        admin.setPassword(hashPassword(admin.getPassword()));

        return userRepository.save(admin);
    }

    @Override
    @Transactional
    public User updateAdmin(User admin, Long adminID) throws AdminException {
        User existingAdmin = userRepository.findById(adminID)
                .orElseThrow(() -> new AdminException(String.format(ADMIN_NOT_FOUND, adminID)));

        updatePassword(existingAdmin, admin.getPassword());

        return userRepository.save(existingAdmin);
    }

    private void validateAdminEmail(String email) throws AdminException {
        User existingAdmin = userRepository.findByEmail(email);
        if (existingAdmin != null) {
            throw new AdminException(String.format(ADMIN_ALREADY_EXISTS, email));
        }
    }

    private Role getOrCreateAdminRole() {
        Role adminRole = roleRepository.findByRole(UserRole.ADMIN);
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setRole(UserRole.ADMIN);
            adminRole = roleRepository.save(adminRole);
        }
        return adminRole;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void updatePassword(User existingAdmin, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            existingAdmin.setPassword(hashPassword(newPassword));
        }
    }

}




