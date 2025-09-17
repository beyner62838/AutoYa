package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Car;
import com.example.back_AutoYa.Entities.CarPhoto;
import com.example.back_AutoYa.dto.CarPhotoDTO;
import com.example.back_AutoYa.repository.CarPhotoRepository;
import com.example.back_AutoYa.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarPhotoService {
    private final CarRepository carRepository;
    private final CarPhotoRepository carPhotoRepository;
    private final StorageService storageService;

    @Transactional
    public CarPhotoDTO upload(Long carId, MultipartFile file) throws Exception {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        String objectKey = storageService.buildObjectKey(carId, file.getOriginalFilename());
        String url = storageService.upload(objectKey, file);

        CarPhoto photo = CarPhoto.builder()
                .car(car)
                .url(url)
                .cover(false)
                .build();
        photo = carPhotoRepository.save(photo);
        // Si el carro no tiene portada aún, la primera foto se vuelve cover
        if (car.getImageUrl() == null) {
            car.setImageUrl(url);
        }
        return new CarPhotoDTO(photo.getId(), photo.getUrl(), photo.isCover(), photo.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CarPhotoDTO> list(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        return carPhotoRepository.findByCarOrderByCreatedAtDesc(car).stream()
                .map(p -> new CarPhotoDTO(p.getId(), p.getUrl(), p.isCover(), p.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long carId, Long photoId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        CarPhoto p = carPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Foto no encontrada"));
        if (!p.getCar().getId().equals(car.getId())) {
            throw new RuntimeException("La foto no pertenece a este vehículo");
        }
        carPhotoRepository.delete(p);
        // Si era cover, limpia portada del Car (simple)
        if (p.isCover() && p.getUrl().equals(car.getImageUrl())) {
            car.setImageUrl(null);
        }
    }

    @Transactional
    public void setCover(Long carId, Long photoId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));
        List<CarPhoto> photos = carPhotoRepository.findByCarOrderByCreatedAtDesc(car);
        for (CarPhoto ph : photos) {
            ph.setCover(ph.getId().equals(photoId));
        }
        carPhotoRepository.saveAll(photos);
        CarPhoto newCover = photos.stream().filter(ph -> ph.getId().equals(photoId)).findFirst()
                .orElseThrow(() -> new RuntimeException("Foto no encontrada"));
        car.setImageUrl(newCover.getUrl());
    }
}
