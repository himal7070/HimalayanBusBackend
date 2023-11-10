package com.himalayanbus.service.implementation;

import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.repository.IAdminRepository;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.service.IAdminService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AdminService implements IAdminService {

    private final IAdminRepository iAdminRepository;
    private final JwtTokenUtil jwtTokenUtil;


    public AdminService(IAdminRepository iAdminRepository, JwtTokenUtil jwtTokenUtil) {
        this.iAdminRepository = iAdminRepository;
        this.jwtTokenUtil = jwtTokenUtil;
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
    public Admin updateAdmin(Admin admin, String jwtToken) throws AdminException {
        Claims claims = jwtTokenUtil.validateJwtToken(jwtToken);

        Integer adminIDFromToken = (Integer) claims.get("sub");
        String adminRoleFromToken = (String) claims.get("role");

        if (!admin.getAdminID().equals(adminIDFromToken) && !adminRoleFromToken.equals("admin")) {
            throw new AdminException("Invalid admin details. You must log in to update the admin information.");
        }

        return iAdminRepository.save(admin);
    }


}
