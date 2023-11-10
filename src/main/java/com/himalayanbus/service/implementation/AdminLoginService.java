package com.himalayanbus.service.implementation;


import com.himalayanbus.exception.AdminException;
import com.himalayanbus.persistence.repository.IAdminRepository;
import com.himalayanbus.persistence.entity.Admin;
import com.himalayanbus.persistence.entity.AdminLoginDTO;
import com.himalayanbus.service.IAdminLoginService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminLoginService implements IAdminLoginService {

    private final IAdminRepository iAdminRepository;
    private final JwtTokenUtil jwtTokenUtil;


    public AdminLoginService(IAdminRepository iAdminRepository, JwtTokenUtil jwtTokenUtil) {
        this.iAdminRepository = iAdminRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public String adminLogin(AdminLoginDTO loginDTO) throws AdminException {
        List<Admin> admins = iAdminRepository.findByEmail(loginDTO.getEmail());

        if (admins.isEmpty()) {
            throw new AdminException("Invalid email. Please provide a valid email.");
        }

        Admin registeredAdmin = admins.get(0);

        if (registeredAdmin == null) {
            throw new AdminException("Invalid email. Please provide a valid email.");
        }

        String adminRole = registeredAdmin.getRole();

        return jwtTokenUtil.generateJwtToken(registeredAdmin.getAdminID(), adminRole);
    }





}
