package com.himalayanbus.persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer routeID;
    private String routeFrom;
    private String routeTo;
    private Integer distance;
}
