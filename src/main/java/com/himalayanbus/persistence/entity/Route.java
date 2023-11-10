package com.himalayanbus.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer routeID;
    private String routeFrom;
    private String routeTo;
    private Integer distance;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<Bus> busList;

    public Route(String routeFrom, String routeTo, Integer distance) {
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.distance = distance;
        this.busList = new ArrayList<>();
    }
}
