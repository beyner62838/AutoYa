package com.example.back_AutoYa.Trace;

/**
 * Enum que define los tipos de acciones registradas en la trazabilidad.
 * Se usa en la entidad TraceLog para identificar el tipo de evento.
 */
public enum TraceAction {

    // ==== RESERVAS ====
    RESERVATION_CREATED,
    RESERVATION_HELD,
    RESERVATION_CANCELLED,

    // ==== PAGOS ====
    PAYMENT_INTENT_CREATED,
    PAYMENT_CAPTURED,
    PAYMENT_CONFIRMED,
    PAYMENT_CREATED,


    // ==== AUTOS ====
    CAR_CREATED,
    CAR_UPDATED,
    CAR_DELETED,

    // ==== TRAZA DE PRUEBA ====
    TRACE_TEST,

    // ==== OTROS ====
    UNKNOWN
}
