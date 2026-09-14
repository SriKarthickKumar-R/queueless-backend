package com.queueless.queuelessbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByDepartmentId(Long departmentId);

    List<Doctor> findByHospitalIdAndDepartmentId(
            Long hospitalId,
            Long departmentId
    );
}