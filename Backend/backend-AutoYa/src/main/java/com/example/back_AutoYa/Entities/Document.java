package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.DocumentType;

public class Document {
    private Long id;
    private DocumentType type;
    private String number;
    private String issuedBy;
    private String issueDate;
    private String expirationDate;

    // Constructors
    public Document() {}

    public Document(Long id, DocumentType type, String number, String issuedBy, String issueDate, String expirationDate) {
        this.id = id;
        this.type = type;
        this.number = number;
        this.issuedBy = issuedBy;
        this.issueDate = issueDate;
        this.expirationDate = expirationDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
}