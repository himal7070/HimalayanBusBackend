package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    User findByEmail(String email);


    default Optional<User> findByUserID() {
        return Optional.empty();
    }
}
