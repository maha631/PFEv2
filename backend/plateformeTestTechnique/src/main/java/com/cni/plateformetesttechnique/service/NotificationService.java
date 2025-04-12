package com.cni.plateformetesttechnique.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.cni.plateformetesttechnique.model.Notification;
import com.cni.plateformetesttechnique.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(String message, String link) {
        Notification notif = new Notification();
        notif.setMessage(message);
        notif.setLu(false);
        notif.setDateNotification(LocalDateTime.now());
        notif.setLink(link); // ← ajoute le lien ici
        notificationRepository.save(notif);
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByLuFalseOrderByDateNotificationDesc();
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "dateNotification"));
    }

    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByLuFalseOrderByDateNotificationDesc();
        for (Notification n : unread) {
            n.setLu(true);
        }
        notificationRepository.saveAll(unread);
    }
}
