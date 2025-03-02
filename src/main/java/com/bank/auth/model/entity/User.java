package com.bank.auth.model.entity;

import com.bank.auth.model.enumeration.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String surname;
    private String password;
    private String email;
    private RoleEnum role;
}
