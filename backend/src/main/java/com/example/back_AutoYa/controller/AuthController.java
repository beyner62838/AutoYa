package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.dto.LoginRequest;
import com.example.back_AutoYa.dto.LoginResponse;
import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.service.AuthService;
import com.example.back_AutoYa.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return authService.register(
                user.getFirstname(),
                user.getLastname(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getProfileImageUrl()
        );
    }

    @GetMapping("/hello")
    public User hello(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return userService.getUserInfoFromToken(token);
    }
}