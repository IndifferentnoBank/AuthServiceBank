package com.bank.auth.controller;

import com.bank.auth.model.dto.input.UserLogin;
import com.bank.auth.model.dto.output.JwtResponse;
import com.bank.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    @PostMapping("give/token")
    @ResponseBody
    public JwtResponse giveToken(@RequestBody UserLogin userId){
        return userService.loginUser(userId.userId());
    }
}
