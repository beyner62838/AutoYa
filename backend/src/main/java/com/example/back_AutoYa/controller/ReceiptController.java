package com.example.back_AutoYa.controller;

import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.repository.ReservationRepository;
import com.example.back_AutoYa.utils.ReceiptGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/autoya/api/receipts")
@CrossOrigin("*")
public class ReceiptController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/{reservationId}")
    public ResponseEntity<String> getReceipt(@PathVariable Long reservationId) {
        Optional<Reservation> opt = reservationRepository.findById(reservationId);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("<h2>No se encontró la reserva con ID " + reservationId + "</h2>");
        }

        Reservation r = opt.get();
        String html = ReceiptGenerator.generateHtml(r);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(html);
    }
}
