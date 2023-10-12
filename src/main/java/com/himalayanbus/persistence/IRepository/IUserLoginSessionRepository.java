package com.himalayanbus.persistence.IRepository;


import com.himalayanbus.persistence.entity.UserLoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserLoginSessionRepository extends JpaRepository<UserLoginSession, Integer> {
    UserLoginSession findBySessionKey(String key);
}
