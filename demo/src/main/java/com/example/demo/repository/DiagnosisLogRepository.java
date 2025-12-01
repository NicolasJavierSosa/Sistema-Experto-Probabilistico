package com.example.demo.repository;

import com.example.demo.model.DiagnosisLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisLogRepository extends JpaRepository<DiagnosisLog, Long> {
}
