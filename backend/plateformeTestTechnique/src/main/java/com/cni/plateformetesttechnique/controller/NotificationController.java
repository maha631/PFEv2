package com.cni.plateformetesttechnique.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cni.plateformetesttechnique.service.NotificationService;
import com.cni.plateformetesttechnique.model.Notification;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*") // Autoriser toutes les origines (peut être restreint selon vos besoins)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Récupérer les notifications non lues
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications();
        if (unreadNotifications.isEmpty()) {
            return ResponseEntity.noContent().build();  // Retourne un code 204 si aucune notification
        }
        return ResponseEntity.ok(unreadNotifications);  // Retourne un code 200 et les notifications non lues
    }

    // Récupérer toutes les notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();  // Retourne un code 204 si aucune notification
        }
        return ResponseEntity.ok(notifications);  // Retourne un code 200 et toutes les notifications
    }

    // Marquer toutes les notifications comme lues
    @PostMapping("/mark-as-read")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            notificationService.markAllAsRead();
            return ResponseEntity.ok().build();  // Retourne un code 200 si tout s'est bien passé
        } catch (Exception e) {
            return ResponseEntity.status(500).build();  // Retourne un code 500 en cas d'erreur serveur
        }
    }
}
