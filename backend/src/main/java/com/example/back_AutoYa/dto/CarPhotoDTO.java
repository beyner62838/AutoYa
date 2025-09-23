package com.example.back_AutoYa.dto;

import java.time.OffsetDateTime;

public record CarPhotoDTO(Long id, String url, boolean cover, OffsetDateTime createdAt) {}
