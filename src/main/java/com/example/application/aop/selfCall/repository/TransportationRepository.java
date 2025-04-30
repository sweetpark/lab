package com.example.application.aop.selfCall.repository;

import com.example.application.aop.selfCall.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
}
