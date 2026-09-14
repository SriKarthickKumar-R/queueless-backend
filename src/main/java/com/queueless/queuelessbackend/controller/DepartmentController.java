package com.queueless.queuelessbackend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueless.queuelessbackend.entity.Department;
import com.queueless.queuelessbackend.repository.DepartmentRepository;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    @GetMapping("/hospital/{hospitalId}")
public List<Department> getDepartmentsByHospital(
        @PathVariable Long hospitalId) {

    return departmentRepository.findByHospitalId(hospitalId);
}

    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentRepository.save(department);
    }
    @GetMapping("/{id}")
public Department getDepartmentById(
        @PathVariable Long id) {

    return departmentRepository
            .findById(id)
            .orElse(null);
}
}