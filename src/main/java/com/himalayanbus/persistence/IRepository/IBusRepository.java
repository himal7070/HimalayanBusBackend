package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusRepository extends JpaRepository<Bus,Integer>  {
    List<Bus> findByBusType(String busType);
    Bus findByRouteFromAndRouteTo(String routeFrom, String routeTo);

}
