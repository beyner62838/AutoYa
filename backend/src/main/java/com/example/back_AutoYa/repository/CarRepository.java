package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT DISTINCT c.city FROM Car c ORDER BY c.city")
    java.util.List<String> findDistinctCities();

    @Query("""
    SELECT c FROM Car c
    WHERE (:city IS NULL OR LOWER(c.city) = LOWER(:city))
      AND (:brand IS NULL OR LOWER(c.brand) LIKE LOWER(CONCAT('%',:brand,'%')))
      AND (:model IS NULL OR LOWER(c.model) LIKE LOWER(CONCAT('%',:model,'%')))
      AND (:category IS NULL OR c.category = :category)
      AND (:transmissionType IS NULL OR c.transmissionType = :transmissionType)
      AND (:color IS NULL OR LOWER(c.color) LIKE LOWER(CONCAT('%',:color,'%')))
    """)
    java.util.List<Car> filter(
        @Param("city") String city,
        @Param("brand") String brand,
        @Param("model") String model,
        @Param("category") com.example.back_AutoYa.Entities.Enums.VehicleCategory category,
        @Param("transmissionType") com.example.back_AutoYa.Entities.Enums.TransmissionType transmissionType,
        @Param("color") String color
    );

}