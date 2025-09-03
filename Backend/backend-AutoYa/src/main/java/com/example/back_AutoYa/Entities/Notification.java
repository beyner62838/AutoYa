package com.example.back_AutoYa.Entities;

import com.example.back_AutoYa.Entities.Enums.NotificationType;

public class Notification {
    private Long id;
    private NotificationType type;
    private String message;
    private String date;
    private boolean read;

    // Constructors
    public Notification() {}

    public Notification(Long id, NotificationType type, String message, String date, boolean read) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.date = date;
        this.read = read;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}