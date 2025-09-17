package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.CarPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarPhotoRepository extends JpaRepository<CarPhoto, Long> {
    List<CarPhoto> findByCarOrderByCreatedAtDesc(Car car);
}
