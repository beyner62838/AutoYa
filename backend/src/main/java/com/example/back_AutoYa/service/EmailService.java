package com.example.back_AutoYa.service;

import com.example.back_AutoYa.Entities.Reservation;
import com.example.back_AutoYa.utils.ReceiptGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ==============================================================
    // 🔹 Enviar correo HTML genérico (ya lo tenías)
    // ==============================================================
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, "utf-8");
        helper.setText(htmlBody, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("no-reply@autoya.example");
        mailSender.send(msg);
    }

    // ==============================================================
    // 🔹 Nuevo método: enviar recibo de reserva
    // ==============================================================
    public void sendReservationReceipt(Reservation reservation) {
        try {
            if (reservation == null || reservation.getClient() == null) {
                System.out.println("⚠️ Reserva o cliente nulos, no se envía correo.");
                return;
            }

            String to = reservation.getClient().getEmail();
            if (to == null || to.isEmpty()) {
                System.out.println("⚠️ Cliente sin correo electrónico, no se envía recibo.");
                return;
            }

            String subject = "Recibo AutoYa - Reserva " + reservation.getId();
            String htmlContent = ReceiptGenerator.generateHtml(reservation);

            sendHtmlEmail(to, subject, htmlContent);

            System.out.println("✅ Recibo enviado correctamente a " + to);

        } catch (MessagingException e) {
            System.out.println("⚠️ Error al enviar el recibo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠️ Error generando o enviando recibo: " + e.getMessage());
        }
    }
}
