package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRouteRepository extends JpaRepository<Route, Integer> {

}
