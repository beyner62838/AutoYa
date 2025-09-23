package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Entities.Enums.UserRole;
import com.example.back_AutoYa.dto.LoginRequest;
import com.example.back_AutoYa.dto.LoginResponse;
import com.example.back_AutoYa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 LOGIN
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generar token con email y rol
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // Devolvemos también el rol
        return new LoginResponse(token, user.getRole().name());
    }

    // 🔹 REGISTER
    public String register(String firstname, String lastname, String phone,
                           String email, String password, UserRole role, String profileImageUrl) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 🔑 Encriptar
        user.setCreationDate(LocalDate.now());

        // <- Aquí el cambio: usar el valor correcto del enum como default
        user.setRole(role != null ? role : UserRole.ADMIN);

        user.setProfileImageUrl(profileImageUrl);

        userRepository.save(user);
        return "User registered successfully";
    }
}