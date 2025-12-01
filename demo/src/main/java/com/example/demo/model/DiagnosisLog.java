package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis_logs")
public class DiagnosisLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    private String bestAction;

    private LocalDateTime createdAt;

    public DiagnosisLog() {
    }

    public DiagnosisLog(String symptoms, String bestAction) {
        this.symptoms = symptoms;
        this.bestAction = bestAction;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getBestAction() {
        return bestAction;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
