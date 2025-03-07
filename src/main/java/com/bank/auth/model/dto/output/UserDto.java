package com.bank.auth.model.dto.output;

import com.bank.auth.model.enumeration.RoleEnum;

import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String phone,
        String fullName,
        String passport,
        Boolean isLocked,
        RoleEnum role
) {
}
