package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public User getUserInfoFromToken(String token) {
        // Validar token y extraer email
        String email = jwtService.validateToken(token);

        // Obtener información del usuario
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public String getWelcomeMessage(String token) {
        User userInfo = getUserInfoFromToken(token);
        return "Hola " + userInfo.getFirstname() + " " + userInfo.getLastname() +
                ". Tu rol es: " + userInfo.getRole();
    }
}
