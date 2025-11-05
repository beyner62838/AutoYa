package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.repository.TraceLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TraceLogService {

    private final TraceLogRepository traceLogRepository;

    public TraceLogService(TraceLogRepository traceLogRepository) {
        this.traceLogRepository = traceLogRepository;
    }

    @Transactional
    public TraceLog save(TraceLog traceLog) {
        return traceLogRepository.save(traceLog);
    }
}
