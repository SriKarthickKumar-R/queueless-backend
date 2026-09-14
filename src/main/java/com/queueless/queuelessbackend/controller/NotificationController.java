package com.queueless.queuelessbackend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.entity.Notification;
import com.queueless.queuelessbackend.repository.NotificationRepository;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }


    // =========================================
    // GET ALL NOTIFICATIONS FOR A PATIENT
    // =========================================

    @GetMapping("/patient/{patientId}")
    public List<Notification> getPatientNotifications(
            @PathVariable Long patientId) {

        return notificationRepository
                .findByPatientIdOrderByCreatedAtDesc(
                        patientId
                );
    }


    // =========================================
    // GET UNREAD NOTIFICATIONS
    // =========================================

    @GetMapping("/patient/{patientId}/unread")
    public List<Notification> getUnreadNotifications(
            @PathVariable Long patientId) {

        return notificationRepository
                .findByPatientIdAndReadStatusFalseOrderByCreatedAtDesc(
                        patientId
                );
    }


    // =========================================
    // GET UNREAD COUNT
    // =========================================

    @GetMapping("/patient/{patientId}/unread/count")
    public long getUnreadCount(
            @PathVariable Long patientId) {

        return notificationRepository
                .countByPatientIdAndReadStatusFalse(
                        patientId
                );
    }


    // =========================================
    // CREATE NOTIFICATION
    // =========================================

    @PostMapping
    public Notification createNotification(
            @RequestBody Notification notification) {

        /*
         * New notifications should always
         * start as unread.
         */

        notification.setReadStatus(false);


        /*
         * Automatically record the creation time.
         */

        notification.setCreatedAt(
                LocalDateTime.now()
        );


        return notificationRepository.save(
                notification
        );
    }


    // =========================================
    // MARK NOTIFICATION AS READ
    // =========================================

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id) {

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElse(null);


        if (notification == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        notification.setReadStatus(true);


        Notification updatedNotification =
                notificationRepository.save(
                        notification
                );


        return ResponseEntity.ok(
                updatedNotification
        );
    }


    // =========================================
    // MARK ALL AS READ
    // =========================================

    @PutMapping("/patient/{patientId}/read-all")
    public ResponseEntity<?> markAllAsRead(
            @PathVariable Long patientId) {

        List<Notification> notifications =
                notificationRepository
                        .findByPatientIdOrderByCreatedAtDesc(
                                patientId
                        );


        for (Notification notification : notifications) {

            notification.setReadStatus(true);

        }


        notificationRepository.saveAll(
                notifications
        );


        return ResponseEntity.ok(
                "All notifications marked as read."
        );
    }
}