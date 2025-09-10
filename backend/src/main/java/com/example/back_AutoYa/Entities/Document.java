package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // <- Anotación crítica
@Table(name = "document")
public class Document {

    @Id // <- Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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