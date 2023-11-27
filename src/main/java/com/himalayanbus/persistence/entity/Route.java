package com.himalayanbus.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Long routeID;
    private String routeFrom;
    private String routeTo;
    private Integer distance;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Bus> busList;

    public Route(String routeFrom, String routeTo, Integer distance) {
        this.routeFrom = routeFrom;
        this.routeTo = routeTo;
        this.distance = distance;
        this.busList = new ArrayList<>();
    }

    public List<Bus> getBusList() {
        if (busList == null) {
            busList = new ArrayList<>();
        }
        return busList;
    }



}
