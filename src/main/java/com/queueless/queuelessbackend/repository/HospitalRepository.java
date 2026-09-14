package com.queueless.queuelessbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueless.queuelessbackend.entity.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}