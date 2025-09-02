package com.autoya.auth.controller;

import com.autoya.auth.dto.LoginRequest;
import com.autoya.auth.dto.LoginResponse;
import com.autoya.auth.model.Entidades.Usuario;
import com.autoya.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public String register(@RequestBody Usuario usuario) {
        authService.register(usuario.getNombres(), usuario.getApellidos(), usuario.getTelefono(),
                usuario.getEmail(), usuario.getPassword(), usuario.getRol(), usuario.getFotoPerfilUrl());
        return "Usuario registrado";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, secured endpoint!";
    }
}
