package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity // <- This annotation is CRITICAL
@Table(name = "users") // Optional, you can change the table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id // <- This annotation is CRITICAL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String phone;

    private LocalDate creationDate;

    private String profileImageUrl;
}