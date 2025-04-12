package com.cni.plateformetesttechnique.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private boolean lu = false;
    private LocalDateTime dateNotification;
    private String link;

    public Notification() {
        this.dateNotification = LocalDateTime.now();
    }

    public Notification(String message) {
        this.message = message;
        this.dateNotification = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public LocalDateTime getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(LocalDateTime dateNotification) {
        this.dateNotification = dateNotification;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

