package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Role;
import com.himalayanbus.persistence.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Role findByRole(UserRole userRole);


}
