package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.dto.CarDTO;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;

    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CarDTO getCarById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.map(this::toDTO).orElse(null);
    }

    public CarDTO createCar(CarDTO carDTO) {
        Car car = toEntity(carDTO);
        Car saved = carRepository.save(car);
        return toDTO(saved);
    }

    public CarDTO updateCar(Long id, CarDTO carDTO) {
        if (!carRepository.existsById(id)) return null;
        Car car = toEntity(carDTO);
        car.setId(id);
        Car updated = carRepository.save(car);
        return toDTO(updated);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    private CarDTO toDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setOwnerId(car.getOwner().getId());
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
        dto.setImageUrl(car.getImageUrl());
        return dto;
    }

    private Car toEntity(CarDTO dto) {
        Car car = new Car();
        car.setId(dto.getId());
        // Busca el usuario propietario por ID
        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow(() -> new RuntimeException("Owner not found"));
        car.setOwner(owner);
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setColor(dto.getColor());
        car.setCategory(dto.getCategory());
        car.setStatus(dto.getStatus());
        car.setFuelType(dto.getFuelType());
        car.setTransmissionType(dto.getTransmissionType());
        car.setPricePerDay(dto.getPricePerDay());
        car.setLicensePlate(dto.getLicensePlate());
        car.setImageUrl(dto.getImageUrl());
        return car;
    }
}