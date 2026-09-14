package com.queueless.queuelessbackend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.entity.Appointment;
import com.queueless.queuelessbackend.repository.AppointmentRepository;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(
            AppointmentRepository appointmentRepository) {

        this.appointmentRepository = appointmentRepository;
    }

    // =========================================
    // GET ALL APPOINTMENTS
    // =========================================

    @GetMapping
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    // =========================================
    // GET PATIENT APPOINTMENTS
    // =========================================

    @GetMapping("/patient/{patientId}")
    public List<Appointment> getPatientAppointments(
            @PathVariable Long patientId) {

        return appointmentRepository
                .findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(
                        patientId
                );
    }

    // =========================================
    // GET DOCTOR APPOINTMENTS FOR A DATE
    // =========================================

    @GetMapping("/doctor/{doctorId}/date/{date}")
    public List<Appointment> getDoctorAppointmentsByDate(
            @PathVariable Long doctorId,
            @PathVariable LocalDate date) {

        return appointmentRepository
                .findByDoctorIdAndAppointmentDate(
                        doctorId,
                        date
                );
    }

    // =========================================
    // CREATE APPOINTMENT
    // =========================================

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestBody Appointment appointment) {

        /*
         * Check whether this doctor already has
         * a CONFIRMED appointment at this exact
         * date and time.
         */

        boolean slotAlreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
                                appointment.getDoctorId(),
                                appointment.getAppointmentDate(),
                                appointment.getAppointmentTime(),
                                "CONFIRMED"
                        );

        /*
         * If the slot is already booked,
         * don't create another appointment.
         */

        if (slotAlreadyBooked) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "This appointment slot is already booked."
                    );
        }

        /*
         * The slot is available,
         * so mark the appointment as confirmed.
         */

        appointment.setStatus("CONFIRMED");

        /*
         * Save the appointment to MySQL.
         */

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        return ResponseEntity.ok(savedAppointment);
    }

    // =========================================
    // CANCEL APPOINTMENT
    // =========================================

    @PutMapping("/{id}/cancel")
    public Appointment cancelAppointment(
            @PathVariable Long id) {

        Appointment appointment =
                appointmentRepository
                        .findById(id)
                        .orElse(null);

        if (appointment == null) {
            return null;
        }

        appointment.setStatus("CANCELLED");

        return appointmentRepository.save(appointment);
    }
}