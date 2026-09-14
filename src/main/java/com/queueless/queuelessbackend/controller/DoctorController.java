package com.queueless.queuelessbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.entity.Doctor;
import com.queueless.queuelessbackend.repository.DoctorRepository;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @GetMapping("/hospital/{hospitalId}/department/{departmentId}")
    public List<Doctor> getDoctorsByHospitalAndDepartment(
            @PathVariable Long hospitalId,
            @PathVariable Long departmentId) {

        return doctorRepository.findByHospitalIdAndDepartmentId(
                hospitalId,
                departmentId
        );
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    @GetMapping("/{id}")
public Doctor getDoctorById(
        @PathVariable Long id) {

    return doctorRepository
            .findById(id)
            .orElse(null);
}
}