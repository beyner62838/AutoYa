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



        if(!occupiedCarIds.isEmpty()) {
            return CarMapper.toDTOList(carRepository.findAll().stream()
                    .filter(car -> !occupiedCarIds.contains(car.getId()))
                    .collect(Collectors.toList()));
        }else {
        return CarMapper.toDTOList(carRepository.findAll());
        }
    }

    public CarDTO getCarById(Long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.map(this::toDTO).orElse(null);
    }

    public List<AvailabilityDTO> getAvailableDatesForCar(Long carId, LocalDate from, LocalDate to) {
        // Buscar reservas de ese carro, ordenadas por fecha inicio
        List<Reservation> reservations = reservationRepository
                .findByCarIdAndEndDateAfterOrderByStartDateAsc(carId, from);

        List<AvailabilityDTO> availability = new ArrayList<>();
        LocalDate currentStart = from;

        for (Reservation res : reservations) {
            // Hay hueco entre la fecha actual y la próxima reserva
            if (currentStart.isBefore(res.getStartDate())) {
                availability.add(new AvailabilityDTO(currentStart, res.getStartDate().minusDays(1)));
            }
            // Avanzamos a después de esta reserva
            if (res.getEndDate().isAfter(currentStart)) {
                currentStart = res.getEndDate().plusDays(1);
            }
        }

        // Último hueco hasta la fecha "to"
        if (currentStart.isBefore(to)) {
            availability.add(new AvailabilityDTO(currentStart, to));
        }

        return availability;
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
        return car;
    }
}