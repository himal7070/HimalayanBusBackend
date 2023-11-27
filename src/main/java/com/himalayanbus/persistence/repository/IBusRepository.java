package com.himalayanbus.persistence.repository;

import com.himalayanbus.persistence.entity.Bus;
import com.himalayanbus.persistence.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusRepository extends JpaRepository<Bus,Long>  {
    List<Bus> findByBusType(String busType);

    Bus findByRouteFromAndRouteTo(String routeFrom, String routeTo);

    Bus findByRoute(Route route);
}
