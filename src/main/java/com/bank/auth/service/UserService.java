package com.bank.auth.service;

import com.bank.auth.exception.ForbiddenException;
import com.bank.auth.exception.NotFoundException;
import com.bank.auth.exception.UnauthorizedException;
import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.model.dto.output.UserDto;
import com.bank.auth.model.entity.DeletedTokens;
import com.bank.auth.model.enumeration.RoleEnum;
import com.bank.auth.model.util.JwtTokenUtils;
import com.bank.auth.repository.DeletedTokensRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {

    private final JwtTokenUtils tokenUtils;
    private final DeletedTokensRepository deletedTokensRepository;
    private final WebClient webClient;
    private final String userServiceUrl = "http://user-service/api/users";

    @SneakyThrows
    public JwtResponse loginUser(UUID userId) {
        if (userId == null) {
            log.error("userId is null");
            throw new NotFoundException("userId cannot be null");
        }

        String token = tokenUtils.generateToken(userId);
        return new JwtResponse(token);
    }

    @SneakyThrows
    public Boolean checkToken(Authentication authentication, String token, List<RoleEnum> roles) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);
        //Отправка запроса в UserService
        UserDto user = sendRequest(userId);


        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("The user is not authorized");
        }

        if (!roles.contains(user.role())) {
            //Добавить исключение для "нет достаточных прав"
            throw new ForbiddenException("The user with ID = " + userId + " does not have the necessary rights");
        }

        return true;
    }

    @SneakyThrows
    public UserDto getProfile(Authentication authentication, String token) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);

        UserDto user = sendRequest(userId);

        //User user = sendRequest(userId);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("The user is not authorized");
        }

        return user;
    }

    @SneakyThrows
    public Boolean logout(Authentication authentication, String token) {
        UUID userId = tokenUtils.getUserIdFromAuthentication(authentication);

        UserDto user = sendRequest(userId);

        if (deletedTokensRepository.findById(token).isPresent()) {
            throw new UnauthorizedException("The user is not authorized");
        }

        DeletedTokens deletedToken = DeletedTokens.of(token);
        deletedTokensRepository.save(deletedToken);
        return true;
    }

    @SneakyThrows
    private UserDto sendRequest(UUID id) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")  // Указываем схему (http/https)
                            .host("51.250.33.133")  // Указываем хост
                            .port(8082)  // Указываем порт
                            .path("/api/users/{id}")
                            .build(id))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            response.bodyToMono(String.class).flatMap(body -> {
                                log.error("Error with request к AuthService: status={}, body={}", response.statusCode(), body);
                                return Mono.error(new WebClientResponseException(
                                        response.statusCode().value(),
                                        "Error with call AuthService",
                                        response.headers().asHttpHeaders(),
                                        body.getBytes(),
                                        StandardCharsets.UTF_8));
                            })
                    )
                    .bodyToMono(UserDto.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error WebClient: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex; // Прокидываем дальше
        }
    }
}
