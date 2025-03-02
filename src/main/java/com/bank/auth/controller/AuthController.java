package com.bank.auth.controller;

import com.bank.auth.model.dto.input.UserLogin;
import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.model.enumeration.RoleEnum;
import com.bank.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("give/token")
    @ResponseBody
    public ResponseEntity<JwtResponse> giveToken(@RequestParam UserLogin userId) {
        return ResponseEntity.ok(userService.loginUser(userId.userId()));
    }

    @PostMapping("check/token")
    @ResponseBody
    public ResponseEntity<Boolean> checkToken(Authentication authentication, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestParam List<RoleEnum> roles) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok(userService.checkToken(authentication, token, roles));
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    @PostMapping("give/profile")
    @ResponseBody
    public ResponseEntity<Object> giveProfile(Authentication authentication, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok(userService.getProfile(authentication, token));
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}
