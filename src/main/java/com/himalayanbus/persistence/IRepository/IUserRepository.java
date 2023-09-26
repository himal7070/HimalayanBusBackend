package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Integer> {

}
