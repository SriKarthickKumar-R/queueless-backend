package com.queueless.queuelessbackend.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.dto.LoginRequest;
import com.queueless.queuelessbackend.entity.Patient;
import com.queueless.queuelessbackend.repository.PatientRepository;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginController(
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Patient login(@RequestBody LoginRequest request) {

        Patient patient =
                patientRepository.findByEmail(request.getEmail());

        if (patient == null) {
            return null;
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        patient.getPassword()
                );

        if (!passwordMatches) {
            return null;
        }

        return patient;
    }
}