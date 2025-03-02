package com.bank.auth.service;

import com.bank.auth.exception.ForbiddenException;
import com.bank.auth.exception.NotFoundException;
import com.bank.auth.exception.UnauthorizedException;
import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.model.entity.DeletedTokens;
import com.bank.auth.model.entity.User;
import com.bank.auth.model.enumeration.RoleEnum;
import com.bank.auth.model.util.JwtTokenUtils;
import com.bank.auth.repository.DeletedTokensRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JwtTokenUtils tokenUtils;
    private final DeletedTokensRepository deletedTokensRepository;
    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://user-service/api/users";

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
        //Отправка запроса в UserService
        User user = sendRequest(userId);

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
        //Отправка запроса в UserService
        User user = sendRequest(userId);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("Пользватель не авторизован");
        }

        return user;
    }

    @SneakyThrows
    public Boolean logout(Authentication authentication, String token) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);
        //Отправка запроса в UserService
        User user = sendRequest(userId);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("Пользватель не авторизован");
        }

        DeletedTokens deletedToken = DeletedTokens.of(token);
        deletedTokensRepository.save(deletedToken);
        return true;
    }

    @SneakyThrows
    private User sendRequest(UUID userId) {
        String url = userServiceUrl + "/" + userId;
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error("Клиентская ошибка при запросе к UserService: {}", ex.getMessage());
            throw new NotFoundException("Пользователь не найден");
        } catch (HttpServerErrorException ex) {
            log.error("Ошибка сервера UserService: {}", ex.getMessage());
            throw new ServiceUnavailableException("Сервис пользователей временно недоступен");
        } catch (ResourceAccessException ex) {
            log.error("Проблема с доступом к UserService: {}", ex.getMessage());
            throw new ServiceUnavailableException("Ошибка подключения к сервису пользователей");
        }
    }
}
