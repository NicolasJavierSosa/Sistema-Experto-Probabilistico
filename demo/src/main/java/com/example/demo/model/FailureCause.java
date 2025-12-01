package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FailureCause {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double priorProbability; // P(F)

    public FailureCause() {
    }

    public FailureCause(String name, Double priorProbability) {
        this.name = name;
        this.priorProbability = priorProbability;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPriorProbability() {
        return priorProbability;
    }

    public void setPriorProbability(Double priorProbability) {
        this.priorProbability = priorProbability;
    }
}
