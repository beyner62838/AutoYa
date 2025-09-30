package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.dto.CarPhotoDTO;
import com.example.back_AutoYa.service.CarPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cars/{carId}/photos")
@RequiredArgsConstructor
public class CarPhotoController {
    private final CarPhotoService service;

    @PostMapping
    public List<CarPhotoDTO> upload(@PathVariable Long carId, @RequestParam("file") List<MultipartFile> files) throws Exception {
        return service.upload(carId, files);
    }

    @GetMapping
    public List<CarPhotoDTO> list(@PathVariable Long carId) {
        return service.list(carId);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long carId, @PathVariable Long photoId) {
        service.delete(carId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{photoId}/cover")
    public ResponseEntity<Void> setCover(@PathVariable Long carId, @PathVariable Long photoId) {
        service.setCover(carId, photoId);
        return ResponseEntity.noContent().build();
    }
}
