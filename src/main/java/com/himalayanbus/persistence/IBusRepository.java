package com.himalayanbus.persistence;

import com.himalayanbus.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBusRepository extends JpaRepository<Bus,Integer>  {
}
