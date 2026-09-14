package com.queueless.queuelessbackend.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.Appointment;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    // Get all appointments of a patient
    List<Appointment>
    findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(
            Long patientId
    );

    // Get all appointments of a doctor on a particular date
    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId,
            LocalDate appointmentDate
    );

    // Check whether a confirmed appointment already exists
    // for this doctor, date and time
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            String status
    );
}