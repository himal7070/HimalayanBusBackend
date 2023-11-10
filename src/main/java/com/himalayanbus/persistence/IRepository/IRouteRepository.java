package com.himalayanbus.persistence.IRepository;

import com.himalayanbus.persistence.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRouteRepository extends JpaRepository<Route, Integer> {
    default Route findByRouteScheduled() {
        return null;
    }

}
