package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.repository.TraceLogRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trace")
public class TraceLogController {

    private final TraceLogRepository traceLogRepository;

    public TraceLogController(TraceLogRepository traceLogRepository) {
        this.traceLogRepository = traceLogRepository;
    }

    /**
     * Endpoint que devuelve todos los registros de trazabilidad.
     * Ejemplo: GET http://localhost:8080/api/trace
     */
    @GetMapping
    public List<TraceLog> getAllTraces() {
        return traceLogRepository.findAll();
    }

    /**
     * Endpoint para consultar por tipo de acción.
     * Ejemplo: GET http://localhost:8080/api/trace/action/PAYMENT_CONFIRMED
     */
    @GetMapping("/action/{action}")
    public List<TraceLog> getTracesByAction(@PathVariable String action) {
        return traceLogRepository.findAll()
                .stream()
                .filter(t -> t.getAction() != null && t.getAction().name().equalsIgnoreCase(action))
                .toList();
    }
}
