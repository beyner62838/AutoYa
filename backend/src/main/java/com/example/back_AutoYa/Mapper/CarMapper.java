package com.example.back_AutoYa.Mapper;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.dto.CarDTO;

import java.util.List;

public class CarMapper {

    public static CarDTO toDTO(Car car) {
        if (car == null) return null;

        return new CarDTO(
                car.getId(),
                car.getOwner() != null ? car.getOwner().getId() : null,
                car.getBrand(),
                car.getModel(),
                car.getYear(),
                car.getColor(),
                car.getCategory(),
                car.getStatus(),
                car.getFuelType(),
                car.getTransmissionType(),
                car.getPricePerDay(),
                car.getLicensePlate()
        );
    }

    public static List<CarDTO> toDTOList(List<Car> cars) {
        return cars.stream()
                .map(CarMapper::toDTO)
                .toList();
    }
}

