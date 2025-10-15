package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.User;
import com.example.back_AutoYa.Mapper.ReservationMapper;
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
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CompletionService completionService;



    public List<ReservationDTO> getAllReservations() {
        List<Reservation> allReservations = reservationRepository.findAll();

        return ReservationMapper.toDTOList(allReservations);
    }

    public List<ReservationDTO> getAllReservationsById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return ReservationMapper.toDTOList(user.get().getReservations());
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


    public String cancelReservation(Long reservationId) {
        // 1. Buscar la reserva
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + reservationId));

        // 2. Reglas de estado: no cancelar si ya está COMPLETED o CANCELLED
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new RuntimeException("No se puede cancelar una reserva completada");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("La reserva ya está cancelada");
        }

        // 3. Regla opcional temporal: no permitir cancelar si ya inició
        LocalDate today = LocalDate.now();
        if (reservation.getStartDate() != null && !reservation.getStartDate().isAfter(today)) {
            throw new RuntimeException("No puedes cancelar una reserva que ya inició o que comienza hoy");
        }

        if (reservation.getStatus() != ReservationStatus.ON_HOLD &&
        reservation.getStatus() != ReservationStatus.IN_PROGRESS &&
        reservation.getStatus() != ReservationStatus.RESERVED){
            throw new RuntimeException("El estado actual no permite cancelar la reserva");
        }
        // 4. Cambiar estado y persistir
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updated = reservationRepository.save(reservation);

        // 5. Retornar DTO
        return "La reserva fue cancelada exitosamente";
    }
}
