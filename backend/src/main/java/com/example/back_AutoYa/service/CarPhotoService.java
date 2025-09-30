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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarPhotoService {
    private final CarRepository carRepository;
    private final CarPhotoRepository carPhotoRepository;
    private final StorageService storageService;

    @Transactional
    public List<CarPhotoDTO> upload(Long carId, List<MultipartFile> files) throws Exception {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        List<CarPhotoDTO> uploaded = new ArrayList<>();

        // Verificar si ya existe portada
        boolean hasCover = carPhotoRepository.existsByCarAndCoverTrue(car);

        for (MultipartFile file : files) {
            String objectKey = storageService.buildObjectKey(carId, file.getOriginalFilename());
            String url = storageService.upload(objectKey, file);

            CarPhoto photo = CarPhoto.builder()
                    .car(car)
                    .url(url)
                    .cover(false)
                    .build();

            // Si no había portada antes, la primera que subimos será cover
            if (!hasCover) {
                photo.setCover(true);
                hasCover = true;
            }

            photo = carPhotoRepository.save(photo);

            uploaded.add(new CarPhotoDTO(
                    photo.getId(),
                    photo.getUrl(),
                    photo.isCover(),
                    photo.getCreatedAt()
            ));
        }

        return uploaded;
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
    }

    @Transactional
    public void setCover(Long carId, Long photoId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        List<CarPhoto> photos = carPhotoRepository.findByCarOrderByCreatedAtDesc(car);

        photos.forEach(ph -> ph.setCover(ph.getId().equals(photoId)));

        photos.stream()
                .filter(ph -> ph.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Foto no encontrada"));

        carPhotoRepository.saveAll(photos);
    }


}