package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Mapper.UserMapper;
import com.example.back_AutoYa.dto.UserDTO;
import com.example.back_AutoYa.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;


    public UserDTO getUserInfoFromToken(String token) {
        // Validar token y extraer email
        String email = jwtService.validateToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Obtener información del usuario
        return UserMapper.toDTO(user);
    }

    public String getWelcomeMessage(String token) {
        UserDTO userInfo = getUserInfoFromToken(token);
        return "Hola " + userInfo.getFirstname() + " " + userInfo.getLastname() +
                ". Tu rol es: " + userInfo.getRole();
    }
}
