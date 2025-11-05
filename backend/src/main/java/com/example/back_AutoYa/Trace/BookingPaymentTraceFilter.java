package com.example.back_AutoYa.Trace;

import com.example.back_AutoYa.Entities.TraceLog;
import com.example.back_AutoYa.service.TraceLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

// Importa correctamente tu enum
// import com.example.back_AutoYa.Entities.TraceAction;

@Component // Importante para ser detectado por Spring
@Order(20) // se ejecuta después de los filtros de autenticación o seguridad
public class BookingPaymentTraceFilter extends OncePerRequestFilter {

    private final TraceLogService traceLogService;
    private final ObjectMapper mapper = new ObjectMapper();

    public BookingPaymentTraceFilter(TraceLogService traceLogService) {
        this.traceLogService = traceLogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().toLowerCase();
        String method = request.getMethod();
        // filtra solo rutas relacionadas con reservas o pagos
        return !(path.startsWith("/autoya/api/reservations") || path.startsWith("/autoya/api/payment")) && !(method.equals("GET"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        RequestBodyCachingWrapper wrappedRequest = new RequestBodyCachingWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;

            TraceLog log = new TraceLog();
            log.setTimeUtc(OffsetDateTime.now(ZoneOffset.UTC));
            log.setHttpMethod(request.getMethod());
            log.setPath(request.getRequestURI());
            log.setHttpStatus(wrappedResponse.getStatus());
            log.setDurationMs(duration);
            log.setClientIp(request.getRemoteAddr());
            log.setUserAgent(Optional.ofNullable(request.getHeader("User-Agent")).orElse(""));
            log.setUsername(Optional.ofNullable(request.getUserPrincipal())
                    .map(p -> p.getName())
                    .orElse("anonymous"));




            byte[] RequestBody = wrappedRequest.getCached();
            if (RequestBody != null && RequestBody.length > 0 &&
                    request.getContentType() != null &&
                    request.getContentType().toLowerCase().contains("application/json")) {

                try {
                    JsonNode requestRoot = mapper.readTree(RequestBody);
                    JsonNode responseRoot = mapper.readTree(wrappedResponse.getContentAsByteArray());
                    String reservationParam = request.getParameter("reservationId");
                    if (reservationParam != null && !reservationParam.isEmpty()) {
                        log.setReservationId(reservationParam);
                    }else {
                        if (mapper.readTree(wrappedResponse.getContentAsByteArray()).has("id")) log.setReservationId(responseRoot.get("id").asText());
                    }
                    if (requestRoot.has("paymentId")) log.setPaymentId(requestRoot.get("paymentId").asText());
                    if (requestRoot.has("amount")) log.setAmount(requestRoot.get("amount").asText());
                    if (requestRoot.has("status")) log.setPaymentStatus(requestRoot.get("status").asText());

                    String preview = new String(RequestBody, 0, Math.min(RequestBody.length, 2048), StandardCharsets.UTF_8);
                    log.setPayloadPreview(preview);
                    log.setPayloadHash(sha256Hex(RequestBody));

                } catch (Exception e) {
                    log.setPayloadPreview("Error parsing JSON RequestBody: " + e.getMessage());
                }
            }

            String uri = request.getRequestURI().toLowerCase();
            String method = request.getMethod();
            System.out.println("[TRACE FILTER:] "+ uri);
            if (uri.contains("/reservations/cancel") || method.equals("DELETE"))
                log.setAction(TraceAction.RESERVATION_CANCELLED);
            else if (uri.contains("/reservations") && method.equals("POST"))
                log.setAction(TraceAction.RESERVATION_CREATED);
            else if (uri.contains("/payment/intent"))
                log.setAction(TraceAction.PAYMENT_INTENT_CREATED);
            else if (uri.contains("/payment/capture"))
                log.setAction(TraceAction.PAYMENT_CAPTURED);
            else if (uri.contains("/payment/confirm"))
                log.setAction(TraceAction.PAYMENT_CONFIRMED);
            else if (uri.contains("/payment") && "failed".equalsIgnoreCase(log.getPaymentStatus()))
                log.setAction(TraceAction.PAYMENT_FAILED);
            else
                log.setAction(TraceAction.UNKNOWN);

            try {
                traceLogService.save(log);

            } catch (Exception e) {
                // Loggea el error, pero no interrumpas la cadena
                e.printStackTrace();
            }finally {
                wrappedResponse.copyBodyToResponse();
            }
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
