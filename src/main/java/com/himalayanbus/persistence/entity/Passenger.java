package com.himalayanbus.persistence.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long passengerId;

    private String firstName;

    private String lastName;

    private String phoneNumber;



    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reservation> reservationList = new ArrayList<>();


    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


}
