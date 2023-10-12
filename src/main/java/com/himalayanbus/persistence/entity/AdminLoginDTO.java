package com.himalayanbus.persistence.entity;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AdminLoginDTO {

    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
