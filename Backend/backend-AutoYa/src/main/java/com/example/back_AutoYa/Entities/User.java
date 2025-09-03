package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.UserRole;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private String phone;

    // Constructors
    public User() {}

    public User(Long id, String name, String email, String password, UserRole role, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}