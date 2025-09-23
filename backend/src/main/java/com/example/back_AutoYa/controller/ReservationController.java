package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.dto.BlockDateDTO;
import com.example.back_AutoYa.dto.ReservationDTO;
import com.example.back_AutoYa.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationDTO> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping("/block-date")
    public ReservationDTO blockDateAndSetPrice(@RequestBody BlockDateDTO dto) {
        return reservationService.blockDateAndSetPrice(dto);
    }

    @PostMapping
    @PostMapping()
    public ReservationDTO createReservation(
            @RequestParam Long clientId,
            @RequestParam Long carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return reservationService.createReservation(clientId, carId, startDate, endDate);
    }

    @PostMapping("/hold")
    public Long holdReservation(@RequestParam Long carId, @RequestParam Long clientId) {
        return reservationService.selectAndHoldReservation(carId, clientId);
    }

}
