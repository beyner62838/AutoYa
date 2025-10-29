package com.example.back_AutoYa.Trace;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.service.TraceLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

/**
 * 🔍 Filtro de trazabilidad (trace_log)
 * Intercepta reservas, pagos y autos. Guarda trazas del request en la BD.
 */
public class BookingPaymentTraceFilter extends OncePerRequestFilter {

    private final TraceLogService traceLogService;
    private final ObjectMapper mapper = new ObjectMapper();

    public BookingPaymentTraceFilter(TraceLogService traceLogService) {
        this.traceLogService = traceLogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        boolean isNotFiltered =!(path.startsWith("/autoya/api/reservations")
                || path.startsWith("/autoya/api/payment")
                || path.startsWith("/autoya/api/cars"));

        return isNotFiltered || method.equalsIgnoreCase("GET");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String contentType = Optional.ofNullable(request.getContentType()).orElse("");
        boolean isMultipart = contentType.contains("multipart/form-data");

        long start = System.currentTimeMillis();

        // ✅ Solo envolver si no es multipart
        RequestBodyCachingWrapper wrappedRequest  = !isMultipart ? new RequestBodyCachingWrapper(request) : null;
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        chain.doFilter(wrappedRequest != null ? wrappedRequest : request, wrappedResponse);

        long duration = System.currentTimeMillis() - start;

        TraceLog log = new TraceLog();
        log.setTimeUtc(OffsetDateTime.now(ZoneOffset.UTC));
        log.setHttpMethod(request.getMethod());
        log.setPath(request.getRequestURI());
        log.setHttpStatus(response.getStatus());
        log.setDurationMs(duration);
        log.setClientIp(request.getRemoteAddr());
        log.setUserAgent(Optional.ofNullable(request.getHeader("User-Agent")).orElse(""));
        log.setUsername(Optional.ofNullable(request.getUserPrincipal())
                .map(p -> p.getName())
                .orElse("anonymous"));

        // ✅ Capturar body o query según tipo de contenido
        if (wrappedRequest != null) {

            byte[] body = wrappedRequest.getCached();

            if (body.length > 0) {
                try {
                    JsonNode wrappedRoot = mapper.readTree(body);
                    JsonNode rootResponse = mapper.readTree(wrappedResponse.getContentAsByteArray());
                    String reservationParam = wrappedRequest.getParameter("reservationId");
                    if (reservationParam != null && !reservationParam.isEmpty()) {
                        log.setReservationId(reservationParam);
                    }else {
                        if (rootResponse.has("id")) log.setReservationId(rootResponse.get("id").asText());
                    }
                    if (wrappedRoot.has("paymentId")) log.setPaymentId(wrappedRoot.get("paymentId").asText());
                    if (wrappedRoot.has("amount")) log.setAmount(wrappedRoot.get("amount").asText());
                    if (wrappedRoot.has("status")) log.setPaymentStatus(wrappedRoot.get("status").asText());

                    String preview = new String(body, 0, Math.min(body.length, 2048), StandardCharsets.UTF_8);
                    log.setPayloadPreview(preview);
                    log.setPayloadHash(sha256Hex(body));
                } catch (Exception e) {
                    log.setPayloadPreview(new String(body, StandardCharsets.UTF_8));
                    log.setPayloadHash(sha256Hex(body));
                }
            } else if (request.getQueryString() != null) {
                log.setPayloadPreview(request.getQueryString());
            }
        } else if (isMultipart) {
            log.setPayloadPreview("[multipart/form-data]");
        }

        // ✅ Detectar acción
        String uri = request.getRequestURI().toLowerCase();
        if (uri.contains("/cancel")) log.setAction(TraceAction.RESERVATION_CANCELLED);
        else if (uri.contains("/hold")) log.setAction(TraceAction.RESERVATION_HELD);
        else if (uri.contains("/intent")) log.setAction(TraceAction.PAYMENT_INTENT_CREATED);
        else if (uri.contains("/capture")) log.setAction(TraceAction.PAYMENT_CAPTURED);
        else if (uri.contains("/confirm")) log.setAction(TraceAction.PAYMENT_CONFIRMED);
        else if (uri.contains("/payment")) log.setAction(TraceAction.PAYMENT_CREATED);
        else if (uri.contains("/cars")) log.setAction(TraceAction.CAR_CREATED);
        else log.setAction(TraceAction.RESERVATION_CREATED);

        // ✅ Guardado seguro: dentro o fuera de transacción
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 🔸 Esperar al commit del negocio (si existe)
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    new Thread(() -> {
                        traceLogService.save(log);
                        System.out.println("✅ TRACE GUARDADO POST-COMMIT: " + log.getAction() + " | " + log.getUsername());
                    }).start();
                }
            });
        } else {
            // 🔸 Sin transacción activa → guardar inmediatamente y de forma asíncrona
            new Thread(() -> {
                traceLogService.save(log);
                System.out.println("✅ TRACE ASÍNCRONO GUARDADO: " + log.getAction() + " | " + log.getUsername());
            }).start();
        }
    }

    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
