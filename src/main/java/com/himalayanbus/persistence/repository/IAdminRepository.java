package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IAdminRepository extends JpaRepository<Admin, Integer> {


    Admin findByEmail(String email);
}
