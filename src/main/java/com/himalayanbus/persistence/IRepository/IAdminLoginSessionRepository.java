package com.himalayanbus.persistence.IRepository;


import com.himalayanbus.persistence.entity.AdminLoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAdminLoginSessionRepository extends JpaRepository<AdminLoginSession, Integer> {

    AdminLoginSession findBySessionKey(String key);

}
