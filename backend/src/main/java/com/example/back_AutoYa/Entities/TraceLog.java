package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Trace.TraceAction;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trace_log", indexes = {
        @Index(name = "idx_trace_time", columnList = "timeUtc"),
        @Index(name = "idx_trace_action", columnList = "action"),
        @Index(name = "idx_trace_reservation", columnList = "reservationId"),
        @Index(name = "idx_trace_payment", columnList = "paymentId"),
        @Index(name = "idx_trace_user", columnList = "username")
})
public class TraceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime timeUtc;
    private String correlationId;

    private String username;
    private String clientIp;
    private String userAgent;

    private String httpMethod;
    private String path;
    private Integer httpStatus;
    private Long durationMs;

    @Enumerated(EnumType.STRING)
    private TraceAction action;

    private String reservationId;
    private String paymentId;
    private String amount;
    private String paymentStatus;

    @Column(length = 2048)
    private String payloadPreview;
    private String payloadHash;

    // Getters y Setters (puedes usar Lombok @Data si ya lo usas)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OffsetDateTime getTimeUtc() { return timeUtc; }
    public void setTimeUtc(OffsetDateTime timeUtc) { this.timeUtc = timeUtc; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Integer getHttpStatus() { return httpStatus; }
    public void setHttpStatus(Integer httpStatus) { this.httpStatus = httpStatus; }

    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }

    public TraceAction getAction() { return action; }
    public void setAction(TraceAction action) { this.action = action; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPayloadPreview() { return payloadPreview; }
    public void setPayloadPreview(String payloadPreview) { this.payloadPreview = payloadPreview; }

    public String getPayloadHash() { return payloadHash; }
    public void setPayloadHash(String payloadHash) { this.payloadHash = payloadHash; }
}