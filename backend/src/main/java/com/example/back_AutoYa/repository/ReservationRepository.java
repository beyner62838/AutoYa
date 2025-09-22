package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCar_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long car_id, LocalDate startDate, LocalDate endDate
    );
    List<Reservation> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate startDate, LocalDate endDate
    );

    List<Reservation>findByCarIdAndEndDateAfterOrderByStartDateAsc(Long carId, LocalDate from);
}
