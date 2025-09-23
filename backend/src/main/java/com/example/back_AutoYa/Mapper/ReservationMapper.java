package com.example.back_AutoYa.Mapper;

import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.service.ReservationService;

import java.util.List;

public class ReservationMapper {
    public static ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setStartDate(reservation.getStartDate()); // 👈 String directo
        dto.setEndDate(reservation.getEndDate());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus().toString());

        dto.setClient(UserMapper.toDTO(reservation.getClient()));
        dto.setCar(CarMapper.toDTO(reservation.getCar()));

        if (reservation.getCar() != null) {
            dto.setOwner(UserMapper.toDTO(reservation.getCar().getOwner()));
        }

        return dto;
    }




    public static List<ReservationDTO> toDTOList(List<Reservation> reservations) {
        return reservations.stream()
                .map(ReservationMapper::toDTO)
                .toList();
    }
}
