package com.example.application.aop.selfCall.repository;

import com.example.application.aop.selfCall.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayRepository extends JpaRepository<Subway, Long> {
}
