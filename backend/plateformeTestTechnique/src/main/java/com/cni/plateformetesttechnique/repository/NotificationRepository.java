package com.cni.plateformetesttechnique.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cni.plateformetesttechnique.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByLuFalseOrderByDateNotificationDesc();
}

