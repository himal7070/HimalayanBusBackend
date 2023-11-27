package com.himalayanbus.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long feedBackID;

    private Integer driverRating;

    private Integer serviceRating;

    private String comments;

    private LocalDateTime feedbackDateAndTime;

}
