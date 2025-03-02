package com.bank.auth.service;

import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.model.util.JwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JwtTokenUtils tokenUtils;

    @SneakyThrows
    public JwtResponse loginUser(UUID userId) {
        if (userId == null) {
            log.error("userId ревен null");
            throw new UsernameNotFoundException("userId не может быть ревен null");
        }

        String token = tokenUtils.generateToken(userId);
        return new JwtResponse(token);
    }
}
