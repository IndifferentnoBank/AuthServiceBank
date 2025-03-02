package com.bank.auth.service;

import com.bank.auth.exception.ForbiddenException;
import com.bank.auth.exception.NotFoundException;
import com.bank.auth.exception.UnauthorizedException;
import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.model.entity.User;
import com.bank.auth.model.enumeration.RoleEnum;
import com.bank.auth.model.util.JwtTokenUtils;
import com.bank.auth.repository.DeletedTokensRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JwtTokenUtils tokenUtils;
    private final DeletedTokensRepository deletedTokensRepository;

    @SneakyThrows
    public JwtResponse loginUser(UUID userId) {
        if (userId == null) {
            log.error("userId ревен null");
            throw new NotFoundException("userId не может быть ревен null");
        }

        String token = tokenUtils.generateToken(userId);
        return new JwtResponse(token);
    }

    @SneakyThrows
    public Boolean checkToken(Authentication authentication, String token, List<RoleEnum> roles) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);
        //отправить запрос в UserService
        //User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User user = new User(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), "Arthur", "Isakhanyan", "Artur2506", "karla-an@mail.ru", RoleEnum.USER);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("Пользватель не авторизован");
        }

        if (!roles.contains(user.getRole())) {
            //Добавить исключение для "нет достаточных прав"
            throw new ForbiddenException("У пользователя с ID = " + userId.toString() + " нет необходимых прав");
        }

        return true;
    }

    @SneakyThrows
    public User getProfile(Authentication authentication, String token) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);
        //отправить запрос в UserService
        //User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        User user = new User(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), "Arthur", "Isakhanyan", "Artur2506", "karla-an@mail.ru", RoleEnum.USER);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("Пользватель не авторизован");
        }

        return user;
    }
}
