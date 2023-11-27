package com.himalayanbus.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long busId;

    private String busName;
    private String driverName;
    private String busType;
    private String routeFrom;
    private String routeTo;
    private LocalDate journeyDate;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private Integer fare;


    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = Objects.requireNonNullElse(availableSeats, 0);
    }


    public Integer getAvailableSeats() {
        return Objects.requireNonNullElse(availableSeats, 0);
    }



    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("busList")
    private Route route;


}