package com.himalayanbus.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class UserLoginSession {

    @Id
    @Column(unique = true)
    private Integer userID;
    private String sessionKey;
    private LocalDateTime time;

}
