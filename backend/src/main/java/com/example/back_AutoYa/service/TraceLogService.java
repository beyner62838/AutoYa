package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.repository.TraceLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de registrar trazas (trace_log) en la base de datos.
 * Se ejecuta en una transacción independiente para no depender del commit del negocio.
 */
@Service
public class TraceLogService {

    private final TraceLogRepository traceLogRepository;

    public TraceLogService(TraceLogRepository traceLogRepository) {
        this.traceLogRepository = traceLogRepository;
    }

    /**
     * Guarda una traza en la base de datos de forma segura e independiente.
     * Usa Propagation.REQUIRES_NEW para abrir una nueva transacción,
     * incluso si no existe una activa (como en los filtros HTTP).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TraceLog save(TraceLog log) {
        try {
            TraceLog saved = traceLogRepository.save(log);
            System.out.println("💾 TRACE INSERTADO EN BD: "
                    + saved.getAction()
                    + " | ID generado: " + saved.getId());
            return saved;
        } catch (Exception e) {
            System.err.println("❌ ERROR AL INSERTAR TRACE EN BD: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
