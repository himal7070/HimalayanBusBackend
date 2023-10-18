package com.himalayanbus.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class AdminLoginSession {
    @Id
    @Column(unique = true)
    private Integer adminID;

    private String sessionKey;

    private LocalDateTime time;

}
