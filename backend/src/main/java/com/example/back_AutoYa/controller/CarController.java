package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.dto.AvailabilityDTO;
import com.example.back_AutoYa.dto.CarDTO;
import com.example.back_AutoYa.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<CarDTO> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping("/avalible")
    public List<CarDTO> getAvalibleCars(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return carService.getAvailableCars(startDate, endDate);
    }
    @GetMapping("/{id}")
    public CarDTO getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping("/{id}/availability")
    public List<AvailabilityDTO> getAvailability(
            @PathVariable Long id,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return carService.getAvailableDatesForCar(id, from, to);
    }

    @PostMapping
    public CarDTO createCar(@RequestBody CarDTO carDTO) {
        return carService.createCar(carDTO);
    }

    @PutMapping("/{id}")
    public CarDTO updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        return carService.updateCar(id, carDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }

    @GetMapping("/cities")
    public java.util.List<String> getCities() {
        return carService.getCities();
    }

    @GetMapping("/filter")
    public java.util.List<com.example.back_AutoYa.dto.CarDTO> filter(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) com.example.back_AutoYa.Entities.Enums.VehicleCategory category,
            @RequestParam(required = false) com.example.back_AutoYa.Entities.Enums.TransmissionType transmissionType,
            @RequestParam(required = false) String color
    ) {
        return carService.filter(city, brand, model, category, transmissionType, color);
    }
}
