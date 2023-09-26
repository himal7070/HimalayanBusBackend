package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAdminRepository extends JpaRepository<Admin, Integer> {

}
