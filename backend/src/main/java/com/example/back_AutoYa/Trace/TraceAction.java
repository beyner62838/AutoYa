package com.example.back_AutoYa.Trace;

/**
 * Enumeración que define los tipos de acciones
 * que se registran en la tabla de trazabilidad (trace_log).
 */
public enum TraceAction {
    // --- Acciones de reservas ---
    RESERVATION_CREATED,
    RESERVATION_HELD,
    RESERVATION_CANCELLED,

    // --- Acciones de pagos ---
    PAYMENT_INTENT_CREATED,
    PAYMENT_CAPTURED,
    PAYMENT_CONFIRMED,
    PAYMENT_FAILED,

    // --- Acción genérica si no se reconoce ---
    UNKNOWN
}
