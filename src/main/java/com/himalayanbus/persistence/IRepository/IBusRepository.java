package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBusRepository extends JpaRepository<Bus,Integer>  {

}
