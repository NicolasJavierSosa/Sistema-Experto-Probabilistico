package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CausalRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FailureCause failureCause;

    @ManyToOne
    private Symptom symptom;

    private Double probability; // P(S|F)

    public CausalRelation() {
    }

    public CausalRelation(FailureCause failureCause, Symptom symptom, Double probability) {
        this.failureCause = failureCause;
        this.symptom = symptom;
        this.probability = probability;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FailureCause getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(FailureCause failureCause) {
        this.failureCause = failureCause;
    }

    public Symptom getSymptom() {
        return symptom;
    }

    public void setSymptom(Symptom symptom) {
        this.symptom = symptom;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }
}
