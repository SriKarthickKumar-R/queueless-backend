package com.queueless.queuelessbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // Get all notifications for a patient
    List<Notification>
    findByPatientIdOrderByCreatedAtDesc(
            Long patientId
    );

    // Get unread notifications for a patient
    List<Notification>
    findByPatientIdAndReadStatusFalseOrderByCreatedAtDesc(
            Long patientId
    );

    // Count unread notifications
    long countByPatientIdAndReadStatusFalse(
            Long patientId
    );
}