package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Mapper.ReservationMapper;
import com.example.back_AutoYa.Mapper.CarMapper;
import com.example.back_AutoYa.Mapper.ReservationMapper;
import com.example.back_AutoYa.Mapper.UserMapper;
import com.example.back_AutoYa.dto.*;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.repository.CarRepository;
import com.example.back_AutoYa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Enums.ReservationStatus;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CompletionService completionService;


    public ReservationService(ReservationRepository reservationRepository, CarRepository carRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    public List<ReservationDTO> getAllReservations() {
        List<Reservation> allReservations = reservationRepository.findAll();

        return ReservationMapper.toDTOList(allReservations);
    }


    public Long selectAndHoldReservation(Long carId, Long clientId){
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.ON_HOLD);
        reservation.setCar(car);
        reservation.setClient(client);
        reservationRepository.save(reservation);
        completionService.deleteHoldReservation(reservation.getId());
        return reservation.getId();
    }



    public ReservationDTO blockDateAndSetPrice(BlockDateDTO dto) {
        Reservation reservation = new Reservation();
        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));
        reservation.setCar(car);
        reservation.setStartDate(dto.getDate());
        reservation.setEndDate(dto.getDate());
        reservation.setBlocked(true);
        reservation.setCustomPrice(dto.getPrice());
        reservation.setStatus(ReservationStatus.BLOCKED); // Asegúrate que exista este estado en tu enum
        reservation.setTotalPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
        reservation.setClient(null); // O asigna el propietario si lo tienes
        return ReservationMapper.toDTO(reservationRepository.save(reservation));
    }




    public ReservationDTO createReservation(Long clientId, Long carId, LocalDate startDate, LocalDate endDate) {
        // 1. Buscar cliente
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + clientId));

        if (!"CLIENT".equalsIgnoreCase(client.getRole().toString())) {
            throw new RuntimeException("El usuario con id " + clientId + " no tiene rol cliente");
        }

        // 2. Buscar carro
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Carro no encontrado con id: " + carId));

        User owner = car.getOwner();
        if (owner == null) {
            throw new RuntimeException("El carro no tiene dueño asignado");
        }

        if (!"ADMIN".equalsIgnoreCase(owner.getRole().toString())) {
            throw new RuntimeException("El dueño del carro no tiene rol OWNER");
        }


        // 3. Crear nueva reservación
        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setCar(car);
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
        reservation.setStatus(ReservationStatus.IN_PROGRESS); // estado inicial
        reservation.setTotalPrice(calculateTotalPrice(car, startDate, endDate));

        //Revisar que no este reservado en esas fechas
        List<Reservation> overlaps = reservationRepository.findByCar_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                reservation.getCar().getId(),
                reservation.getEndDate(),
                reservation.getStartDate()
        );
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("La fecha escogida no esta disponible para poder reservar el carro.");
        }

        // 4. Guardar en DB
        Reservation saved = reservationRepository.save(reservation);

        // 5. Retornar DTO limpio
        return ReservationMapper.toDTO(saved);
    }



    private Double calculateTotalPrice(Car car, LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            throw new RuntimeException("La fecha de fin debe ser posterior a la de inicio");
        }
        return days * car.getPricePerDay();
    }

}
