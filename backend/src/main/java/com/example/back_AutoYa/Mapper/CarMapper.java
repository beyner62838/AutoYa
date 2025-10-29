package com.example.back_AutoYa.Mapper;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.dto.CarDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    public static CarDTO toDTO(Car car) {
        if (car == null) return null;

        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setOwnerId(car.getOwner() != null ? car.getOwner().getId() : null);
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());
        dto.setColor(car.getColor());
        dto.setCategory(car.getCategory());
        dto.setStatus(car.getStatus());
        dto.setFuelType(car.getFuelType());
        dto.setTransmissionType(car.getTransmissionType());
        dto.setPricePerDay(car.getPricePerDay());
        dto.setLicensePlate(car.getLicensePlate());
        dto.setCity(car.getCity());
        return dto;
    }

    public static List<CarDTO> toDTOList(List<Car> cars) {
        if (cars == null) return java.util.Collections.emptyList();
        return cars.stream().map(CarMapper::toDTO).collect(Collectors.toList());
    }
}
