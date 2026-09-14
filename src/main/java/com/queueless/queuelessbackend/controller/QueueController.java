package com.queueless.queuelessbackend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.dto.QueuePatientResponse;
import com.queueless.queuelessbackend.entity.Patient;
import com.queueless.queuelessbackend.entity.QueueEntry;
import com.queueless.queuelessbackend.repository.PatientRepository;
import com.queueless.queuelessbackend.repository.QueueEntryRepository;

@RestController
@RequestMapping("/queues")
public class QueueController {

    private final QueueEntryRepository queueEntryRepository;
    private final PatientRepository patientRepository;

    public QueueController(
            QueueEntryRepository queueEntryRepository,
            PatientRepository patientRepository) {

        this.queueEntryRepository =
                queueEntryRepository;

        this.patientRepository =
                patientRepository;
    }


    // =========================================
    // JOIN QUEUE
    // =========================================

    @PostMapping("/join")
    public QueueEntry joinQueue(
            @RequestBody QueueEntry request) {

        LocalDate today = LocalDate.now();


        // Check whether this patient is already
        // waiting for this doctor today

        QueueEntry existingQueue =
                queueEntryRepository
                        .findByPatientIdAndDoctorIdAndQueueDateAndStatus(
                                request.getPatientId(),
                                request.getDoctorId(),
                                today,
                                "WAITING"
                        );


        // If already waiting, return existing token

        if (existingQueue != null) {
            return existingQueue;
        }


        // Get today's queue

        List<QueueEntry> todayQueue =
                queueEntryRepository
                        .findByDoctorIdAndQueueDate(
                                request.getDoctorId(),
                                today
                        );


        // Find the highest token number

        int nextToken =
                todayQueue.stream()
                        .map(QueueEntry::getTokenNumber)
                        .max(Comparator.naturalOrder())
                        .orElse(0) + 1;


        // Create new queue entry

        QueueEntry queueEntry =
                new QueueEntry();


        queueEntry.setPatientId(
                request.getPatientId()
        );

        queueEntry.setHospitalId(
                request.getHospitalId()
        );

        queueEntry.setDepartmentId(
                request.getDepartmentId()
        );

        queueEntry.setDoctorId(
                request.getDoctorId()
        );

        queueEntry.setTokenNumber(
                nextToken
        );

        queueEntry.setStatus(
                "WAITING"
        );

        queueEntry.setQueueDate(
                today
        );

        queueEntry.setJoinedAt(
                LocalDateTime.now()
        );


        // Save to database

        return queueEntryRepository.save(
                queueEntry
        );
    }



    // =========================================
    // GET TODAY'S QUEUE FOR DOCTOR
    // =========================================

    @GetMapping("/doctor/{doctorId}/today")
    public List<QueuePatientResponse> getTodayQueue(
            @PathVariable Long doctorId) {

        LocalDate today =
                LocalDate.now();


        // Get today's queue entries

        List<QueueEntry> queueEntries =
                queueEntryRepository
                        .findByDoctorIdAndQueueDate(
                                doctorId,
                                today
                        );


        // Convert QueueEntry into
        // QueuePatientResponse

        return queueEntries.stream()
                .map(queueEntry -> {

                    // Find the patient using patientId

                    Patient patient =
                            patientRepository
                                    .findById(
                                            queueEntry.getPatientId()
                                    )
                                    .orElse(null);


                    // Create response object

                    QueuePatientResponse response =
                            new QueuePatientResponse();


                    // Queue information

                    response.setId(
                            queueEntry.getId()
                    );

                    response.setPatientId(
                            queueEntry.getPatientId()
                    );

                    response.setHospitalId(
                            queueEntry.getHospitalId()
                    );

                    response.setDepartmentId(
                            queueEntry.getDepartmentId()
                    );

                    response.setDoctorId(
                            queueEntry.getDoctorId()
                    );

                    response.setTokenNumber(
                            queueEntry.getTokenNumber()
                    );

                    response.setStatus(
                            queueEntry.getStatus()
                    );


                    // Patient information

                    if (patient != null) {

                        response.setPatientName(
                                patient.getName()
                        );

                        response.setPatientPhone(
                                patient.getPhone()
                        );
                    }


                    return response;

                })
                .toList();
    }



    // =========================================
    // CALL NEXT PATIENT
    // =========================================

    @PutMapping("/doctor/{doctorId}/call-next")
    public QueueEntry callNext(
            @PathVariable Long doctorId) {

        LocalDate today =
                LocalDate.now();


        // Check whether somebody is already
        // being served

        QueueEntry currentPatient =
                queueEntryRepository
                        .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
                                doctorId,
                                today,
                                "SERVING"
                        );


        // Don't call another patient

        if (currentPatient != null) {
            return currentPatient;
        }


        // Find the next waiting patient

        QueueEntry nextPatient =
                queueEntryRepository
                        .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
                                doctorId,
                                today,
                                "WAITING"
                        );


        // Nobody is waiting

        if (nextPatient == null) {
            return null;
        }


        // Change status

        nextPatient.setStatus(
                "SERVING"
        );


        return queueEntryRepository.save(
                nextPatient
        );
    }



    // =========================================
    // COMPLETE PATIENT
    // =========================================

    @PutMapping("/{queueId}/complete")
    public QueueEntry completePatient(
            @PathVariable Long queueId) {

        LocalDate today =
                LocalDate.now();


        // Find patient

        QueueEntry currentPatient =
                queueEntryRepository
                        .findById(queueId)
                        .orElse(null);


        if (currentPatient == null) {
            return null;
        }


        // Mark current patient as completed

        currentPatient.setStatus(
                "COMPLETED"
        );


        QueueEntry completedPatient =
                queueEntryRepository.save(
                        currentPatient
                );


        // Automatically call next patient

        QueueEntry nextPatient =
                queueEntryRepository
                        .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
                                currentPatient.getDoctorId(),
                                today,
                                "WAITING"
                        );


        if (nextPatient != null) {

            nextPatient.setStatus(
                    "SERVING"
            );

            queueEntryRepository.save(
                    nextPatient
            );
        }


        return completedPatient;
    }



    // =========================================
    // HOLD PATIENT
    // =========================================

    @PutMapping("/{queueId}/hold")
    public QueueEntry holdPatient(
            @PathVariable Long queueId) {

        LocalDate today =
                LocalDate.now();


        // Find patient

        QueueEntry currentPatient =
                queueEntryRepository
                        .findById(queueId)
                        .orElse(null);


        if (currentPatient == null) {
            return null;
        }


        // Change current patient to HOLD

        currentPatient.setStatus(
                "HOLD"
        );


        QueueEntry heldPatient =
                queueEntryRepository.save(
                        currentPatient
                );


        // Automatically call next waiting patient

        QueueEntry nextPatient =
                queueEntryRepository
                        .findFirstByDoctorIdAndQueueDateAndStatusOrderByTokenNumberAsc(
                                currentPatient.getDoctorId(),
                                today,
                                "WAITING"
                        );


        if (nextPatient != null) {

            nextPatient.setStatus(
                    "SERVING"
            );

            queueEntryRepository.save(
                    nextPatient
            );
        }


        return heldPatient;
    }



    // =========================================
    // RECALL PATIENT
    // =========================================

    @PutMapping("/{queueId}/recall")
    public QueueEntry recallPatient(
            @PathVariable Long queueId) {

        QueueEntry patient =
                queueEntryRepository
                        .findById(queueId)
                        .orElse(null);


        if (patient == null) {
            return null;
        }


        // Change HOLD back to WAITING

        patient.setStatus(
                "WAITING"
        );


        return queueEntryRepository.save(
                patient
        );
    }



    // =========================================
    // CANCEL QUEUE
    // =========================================

    @PutMapping("/{queueId}/cancel")
    public QueueEntry cancelQueue(
            @PathVariable Long queueId) {

        QueueEntry queueEntry =
                queueEntryRepository
                        .findById(queueId)
                        .orElse(null);


        if (queueEntry == null) {
            return null;
        }


        // Only WAITING or HOLD patients can cancel

        if (!queueEntry.getStatus().equals("WAITING")
                && !queueEntry.getStatus().equals("HOLD")) {

            return queueEntry;
        }


        queueEntry.setStatus(
                "CANCELLED"
        );


        return queueEntryRepository.save(
                queueEntry
        );
    }
    @GetMapping("/patient/{patientId}/history")
public List<QueueEntry> getPatientHistory(
        @PathVariable Long patientId) {

    return queueEntryRepository
            .findByPatientIdOrderByQueueDateDescJoinedAtDesc(
                    patientId
            );
}
}