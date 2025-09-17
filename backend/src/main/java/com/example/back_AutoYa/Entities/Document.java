package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relación con User ---
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(nullable = false)
    private String number;

    @Column(name = "issued_by", nullable = false)
    private String issuedBy;

    @Column(name = "issue_date", nullable = false)
    private String issueDate;

    @Column(name = "expiration_date", nullable = false)
    private String expirationDate;
}
