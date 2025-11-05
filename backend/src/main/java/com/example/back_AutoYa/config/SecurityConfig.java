package com.example.back_AutoYa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Value("${cors.allowed-origins}")
    private String corsAllowedOrigins;

    @Value("${cors.allowed-methods}")
    private String corsAllowedMethods;

    // leemos el context-path para construir patrones con y sin prefijo
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    private String p(String pattern) {
        // devuelve patrón sin prefijo y con prefijo (/autoya)
        // ej. "/api/cars" -> ["/api/cars", "/autoya/api/cars"]
        return pattern;
    }

    private String withCtx(String pattern) {
        if (contextPath == null || contextPath.isBlank() || "/".equals(contextPath)) {
            return pattern;
        }
        // asegura que el contextPath empieza con "/"
        String ctx = contextPath.startsWith("/") ? contextPath : ("/" + contextPath);
        // evita doble slash
        if (pattern.startsWith("/")) {
            return ctx + pattern;
        }
        return ctx + "/" + pattern;
    }

    private String[] both(String... patterns) {
        List<String> out = new ArrayList<>();
        for (String p : patterns) {
            out.add(p);
            out.add(withCtx(p));
        }
        return out.toArray(new String[0]);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // usa el bean corsConfigurationSource()
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // preflight + auth pública
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(both("/auth/**", "/actuator/**")).permitAll()

                        // ====== GET PÚBLICOS ======
                        .requestMatchers(HttpMethod.GET, both(
                                "/api/trace/**"
                        )).permitAll()

                        // ====== EL RESTO PROTEGIDO ======
                        .requestMatchers(both("/api/admin/**")).hasRole("ADMIN")
                        .requestMatchers(both("/api/client/**")).hasRole("CLIENT")
                        // Cars: escritura/acciones requieren auth
                        .requestMatchers(both("/api/cars/**")).authenticated()
                        .requestMatchers(both("/api/reservations/**")).authenticated()
                        .requestMatchers(both("/api/payments/**")).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(",")).map(String::trim).toList();
        List<String> methods = Arrays.stream(corsAllowedMethods.split(",")).map(String::trim).toList();

        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origins);
        cfg.setAllowedMethods(methods);
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
