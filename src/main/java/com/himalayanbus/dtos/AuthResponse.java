package com.himalayanbus.dtos;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String role;

}
