package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Mapper.CarMapper;
import com.example.back_AutoYa.dto.AvailabilityDTO;
import com.example.back_AutoYa.dto.CarDTO;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// ⬇️ caché
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CarDTO> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        // Reservas que se cruzan con el rango
        List<Reservation> reservations = reservationRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);

        // IDs de carros ocupados
        Set<Long> occupiedCarIds = reservations.stream()
                .map(res -> res.getCar().getId())
                .collect(Collectors.toSet());

        if (!occupiedCarIds.isEmpty()) {
            return CarMapper.toDTOList(
                    carRepository.findAll().stream()
                            .filter(car -> !occupiedCarIds.contains(car.getId()))
                            .collect(Collectors.toList())
            );
        } else {
            return CarMapper.toDTOList(carRepository.findAll());
        }
    }

    //  Lee desde caché (clave = id). Evita cachear null. sync para evitar stampede.
    @Cacheable(value = "carById", key = "#id", unless = "#result == null", sync = true)
    public CarDTO getCarById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.map(this::toDTO).orElse(null);
    }

    public List<AvailabilityDTO> getAvailableDatesForCar(Long carId, LocalDate from, LocalDate to) {
        List<Reservation> reservations = reservationRepository
                .findByCarIdAndEndDateAfterOrderByStartDateAsc(carId, from);

        List<AvailabilityDTO> availability = new ArrayList<>();
        LocalDate currentStart = from;

        for (Reservation res : reservations) {
            if (currentStart.isBefore(res.getStartDate())) {
                availability.add(new AvailabilityDTO(currentStart, res.getStartDate().minusDays(1)));
            }
            if (res.getEndDate().isAfter(currentStart)) {
                currentStart = res.getEndDate().plusDays(1);
            }
        }

        if (currentStart.isBefore(to)) {
            availability.add(new AvailabilityDTO(currentStart, to));
        }

        return availability;
    }

    // Guarda y pone/actualiza caché del item creado (clave = id retornado)
    @CachePut(value = "carById", key = "#result.id", unless = "#result == null")
    public CarDTO createCar(CarDTO carDTO) {
        Car car = toEntity(carDTO);
        Car saved = carRepository.save(car);
        return toDTO(saved);
    }

    // Actualiza DB y refresca caché del id actualizado
    @CachePut(value = "carById", key = "#id", unless = "#result == null")
    public CarDTO updateCar(Long id, CarDTO carDTO) {
        if (!carRepository.existsById(id)) return null;
        Car car = toEntity(carDTO);
        car.setId(id);
        Car updated = carRepository.save(car);
        return toDTO(updated);
    }

    // Elimina y limpia la entrada del caché
    @CacheEvict(value = "carById", key = "#id")
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
        dto.setCity(car.getCity());
        return dto;
    }

    private Car toEntity(CarDTO dto) {
        Car car = new Car();
        car.setId(dto.getId());
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));
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
        car.setCity(dto.getCity());
        return car;
    }

    public List<String> getCities() {
        return carRepository.findDistinctCities();
    }

    public List<com.example.back_AutoYa.dto.CarDTO> filter(
            String city, String brand, String model,
            com.example.back_AutoYa.Entities.Enums.VehicleCategory category,
            com.example.back_AutoYa.Entities.Enums.TransmissionType transmissionType,
            String color) {

        var list = carRepository.filter(
                (city == null || city.isBlank()) ? null : city,
                (brand == null || brand.isBlank()) ? null : brand,
                (model == null || model.isBlank()) ? null : model,
                category,
                transmissionType,
                (color == null || color.isBlank()) ? null : color
        );
        return com.example.back_AutoYa.Mapper.CarMapper.toDTOList(list);
    }
}
