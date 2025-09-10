package com.example.back_AutoYa.service;

import com.example.back_AutoYa.dto.LoginRequest;
import com.example.back_AutoYa.dto.LoginResponse;
import com.example.back_AutoYa.Entities.Enums.UserRole;
import com.example.back_AutoYa.Entities.User;
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

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail());
        return new LoginResponse(token);
    }

    public void register(String firstname, String lastname, String phone,
                         String email, String password, UserRole role, String profileImageUrl) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setCreationDate(LocalDate.now());
        user.setProfileImageUrl(profileImageUrl);

        userRepository.save(user);
    }
}
