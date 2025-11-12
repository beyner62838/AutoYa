package com.example.back_AutoYa.repository;

import com.example.back_AutoYa.Entities.TraceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraceLogRepository extends JpaRepository<TraceLog, Long> {
}
