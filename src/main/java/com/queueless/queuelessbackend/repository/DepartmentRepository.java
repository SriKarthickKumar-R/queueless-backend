package com.queueless.queuelessbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.Department;
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByHospitalId(Long hospitalId);
}
