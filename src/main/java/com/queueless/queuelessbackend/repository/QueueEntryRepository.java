package com.queueless.queuelessbackend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.QueueEntry;

public interface QueueEntryRepository
        extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByDoctorIdAndQueueDate(
            Long doctorId,
            LocalDate queueDate
    );

    QueueEntry findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
            Long doctorId,
            LocalDate queueDate,
            String status
    );

    QueueEntry findByPatientIdAndDoctorIdAndQueueDateAndStatus(
            Long patientId,
            Long doctorId,
            LocalDate queueDate,
            String status
    );
    List<QueueEntry> findByPatientIdOrderByQueueDateDescJoinedAtDesc(
        Long patientId
);
}