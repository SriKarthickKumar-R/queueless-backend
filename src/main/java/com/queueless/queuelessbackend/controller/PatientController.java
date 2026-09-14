package com.queueless.queuelessbackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.entity.Patient;
import com.queueless.queuelessbackend.repository.PatientRepository;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientController(
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================
    // GET ALL PATIENTS
    // =========================================

    @GetMapping
    public List<Patient> getAllPatients() {

        return patientRepository.findAll();
    }

    // =========================================
    // GET ONE PATIENT
    // =========================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(
            @PathVariable Long id) {

        Patient patient =
                patientRepository
                        .findById(id)
                        .orElse(null);

        if (patient == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(patient);
    }

    // =========================================
    // CREATE PATIENT
    // =========================================

    @PostMapping
    public Patient createPatient(
            @RequestBody Patient patient) {

        /*
         * Convert the plain-text password
         * into a BCrypt hashed password
         * before saving it.
         */

        String hashedPassword =
                passwordEncoder.encode(
                        patient.getPassword()
                );

        patient.setPassword(hashedPassword);

        return patientRepository.save(patient);
    }

    // =========================================
    // UPDATE PATIENT PROFILE
    // =========================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient updatedPatient) {

        /*
         * Find the existing patient.
         */

        Patient patient =
                patientRepository
                        .findById(id)
                        .orElse(null);

        /*
         * Patient doesn't exist.
         */

        if (patient == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        /*
         * Update profile information.
         */

        patient.setName(
                updatedPatient.getName()
        );

        patient.setEmail(
                updatedPatient.getEmail()
        );

        patient.setPhone(
                updatedPatient.getPhone()
        );

        /*
         * Update role only when a role
         * is actually provided.
         */

        if (updatedPatient.getRole() != null
                && !updatedPatient.getRole().isBlank()) {

            patient.setRole(
                    updatedPatient.getRole()
            );
        }

        /*
         * We deliberately DON'T update
         * the password here.
         */

        Patient savedPatient =
                patientRepository.save(
                        patient
                );

        return ResponseEntity.ok(
                savedPatient
        );
    }
}