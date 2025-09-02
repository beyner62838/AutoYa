package com.autoya.auth.service;

import com.autoya.auth.dto.LoginRequest;
import com.autoya.auth.dto.LoginResponse;
import com.autoya.auth.model.Entidades.Enums.RolUsuario;
import com.autoya.auth.model.Entidades.Usuario;
import com.autoya.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(usuario.getEmail());
        return new LoginResponse(token);
    }

    public void register(String nombres, String apellidos, String telefono
            , String email, String password, RolUsuario rol, String foto_perfil_url) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Usuario ya existe");
        }
        Usuario user = new Usuario();
        user.setNombres(nombres);
        user.setApellidos(apellidos);
        user.setTelefono(telefono);

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRol(rol);

        user.setFechaCreacion(LocalDate.now());
        user.setFotoPerfilUrl(foto_perfil_url);

        usuarioRepository.save(user);
    }
}
