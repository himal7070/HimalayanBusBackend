package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusRepository extends JpaRepository<Bus,Long>  {
    List<Bus> findByBusType(String busType);


    List<Bus> findByRoute_RouteFromAndRoute_RouteTo(String routeFrom, String routeTo);

}
