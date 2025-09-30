package com.example.back_AutoYa.Mapper;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.dto.UserDTO;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString()); // 👈 mapeamos el role
        return dto;
    }
}
