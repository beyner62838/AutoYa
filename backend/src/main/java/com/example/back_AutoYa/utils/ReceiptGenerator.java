package com.example.back_AutoYa.utils;

import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.Entities.Payment;

import java.time.format.DateTimeFormatter;

public class ReceiptGenerator {

    public static String generateHtml(Reservation r) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder sb = new StringBuilder();

        sb.append("<!doctype html><html><head><meta charset='utf-8'><title>Recibo - Reserva ")
                .append(r.getId())
                .append("</title>")
                .append("<style>")
                .append("body{font-family:Arial,Helvetica,sans-serif;margin:20px;color:#222}")
                .append(".card{max-width:800px;margin:0 auto;padding:20px;border:1px solid #eee;border-radius:6px;box-shadow:0 2px 6px rgba(0,0,0,0.05)}")
                .append("h1{font-size:20px;margin-bottom:0}")
                .append("table{width:100%;border-collapse:collapse;margin-top:12px}")
                .append("td,th{padding:8px;border-bottom:1px solid #f0f0f0;text-align:left}")
                .append(".right{text-align:right}")
                .append(".muted{color:#666;font-size:13px}")
                .append("</style>")
                .append("</head><body>")
                .append("<div class='card'>")
                .append("<h1>Recibo / Comprobante</h1>")
                .append("<div class='muted'>ID Reserva: ").append(r.getId()).append("</div>")
                .append("<p></p>");

        // Detalles de la reserva
        sb.append("<h2>Detalles de la reserva</h2>");
        sb.append("<table>");
        try {
            if (r.getClient() != null) {
                sb.append(row("Cliente", r.getClient().getFirstname() + " " + r.getClient().getLastname()));
                sb.append(row("Email", r.getClient().getEmail()));
                sb.append(row("Teléfono", r.getClient().getPhone()));
            } else {
                sb.append(row("Cliente", "-"));
            }
        } catch (Exception e) {
            sb.append(row("Cliente", "-"));
        }

        try {
            if (r.getCar() != null) {
                String placa = r.getCar().getLicensePlate() != null ? r.getCar().getLicensePlate() : "";
                sb.append(row("Vehículo", r.getCar().getBrand() + " " + r.getCar().getModel() + " (" + placa + ")"));
            } else {
                sb.append(row("Vehículo", "-"));
            }
        } catch (Exception e) {
            sb.append(row("Vehículo", "-"));
        }

        try {
            sb.append(row("Fecha inicio", r.getStartDate() != null ? r.getStartDate().format(df) : "-"));
            sb.append(row("Fecha fin", r.getEndDate() != null ? r.getEndDate().format(df) : "-"));
        } catch (Exception ignored) {}

        sb.append(row("Estado", r.getStatus() != null ? r.getStatus().name() : "-"));
        sb.append(row("Precio total", String.format("%.2f", r.getTotalPrice())));

        sb.append("</table>");

        // Pagos
        sb.append("<h2>Pagos</h2>");
        sb.append("<table>");
        sb.append("<tr><th>Id</th><th>Método</th><th>Estado</th><th>Monto</th><th>Fecha</th></tr>");
        if (r.getPayments() != null && !r.getPayments().isEmpty()) {
            for (Payment p : r.getPayments()) {
                String date = p.getDate() != null ? p.getDate().toString() : "-";
                sb.append("<tr>")
                        .append(td(String.valueOf(p.getId())))
                        .append(td(p.getMethod() != null ? p.getMethod().name() : "-"))
                        .append(td(p.getStatus() != null ? p.getStatus().name() : "-"))
                        .append(td(String.format("%.2f", p.getAmount())))
                        .append(td(date))
                        .append("</tr>");
            }
        } else {
            sb.append("<tr><td colspan='5' class='muted'>No hay pagos registrados</td></tr>");
        }
        sb.append("</table>");

        sb.append("<p class='muted'>Generado por AutoYa</p>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    private static String row(String k, String v) {
        return "<tr><th style='text-align:left;padding:8px;border-bottom:1px solid #f0f0f0;width:30%'>" + escape(k) + "</th><td>" + escape(v) + "</td></tr>";
    }
    private static String td(String v) { return "<td>" + escape(v) + "</td>"; }

    private static String escape(Object o) {
        if (o == null) return "";
        String s = String.valueOf(o);
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;").replace("'","&#39;");
    }
}
