package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.Trace.TraceAction;
import com.example.back_AutoYa.service.TraceLogService;
import com.example.back_AutoYa.repository.TraceLogRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Controlador para consultar y crear registros de trazabilidad.
 * Acceso exclusivo para usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/trace")
public class TraceController {

    private final TraceLogRepository traceLogRepository;
    private final TraceLogService traceLogService;

    public TraceController(TraceLogRepository traceLogRepository, TraceLogService traceLogService) {
        this.traceLogRepository = traceLogRepository;
        this.traceLogService = traceLogService;
    }

    /**
     * ✅ Obtener todos los registros de trazabilidad, ordenados del más reciente al más antiguo.
     * Solo accesible para ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TraceLog> getAllTraces() {
        return traceLogRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .toList();
    }

    /**
     * ✅ Filtrar registros por usuario (correo/username).
     * Ejemplo: /api/trace/user?username=cliente@mail.com
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TraceLog> getTracesByUsername(@RequestParam String username) {
        return traceLogRepository.findAll()
                .stream()
                .filter(t -> username.equalsIgnoreCase(t.getUsername()))
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .toList();
    }

    /**
     * ✅ Endpoint de prueba para insertar manualmente una traza.
     * Ejemplo: POST /api/trace/test
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public TraceLog createTestTrace() {
        TraceLog log = new TraceLog();

        // 🧩 Puedes cambiar el tipo de acción aquí
        log.setAction(TraceAction.TRACE_TEST);
        log.setUsername("admin@autoya.com");
        log.setPath("/api/trace/test");
        log.setHttpMethod("POST");
        log.setHttpStatus(200);
        log.setClientIp("127.0.0.1");
        log.setUserAgent("Postman");
        log.setTimeUtc(OffsetDateTime.now(ZoneOffset.UTC));

        TraceLog saved = traceLogService.save(log);
        System.out.println("🧪 TRACE DE PRUEBA CREADO: " + saved.getId());

        return saved;
    }
}
