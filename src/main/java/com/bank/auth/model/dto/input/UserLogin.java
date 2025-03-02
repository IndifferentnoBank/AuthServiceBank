package com.bank.auth.model.dto.input;

import java.util.UUID;

public record UserLogin(
        UUID userId
) {
}
