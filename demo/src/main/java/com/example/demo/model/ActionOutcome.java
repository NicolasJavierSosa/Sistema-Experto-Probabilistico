package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ActionOutcome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private RepairAction repairAction;

    @ManyToOne
    private FailureCause failureCause;

    private Double utility; // U(A, F)

    public ActionOutcome() {
    }

    public ActionOutcome(RepairAction repairAction, FailureCause failureCause, Double utility) {
        this.repairAction = repairAction;
        this.failureCause = failureCause;
        this.utility = utility;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RepairAction getRepairAction() {
        return repairAction;
    }

    public void setRepairAction(RepairAction repairAction) {
        this.repairAction = repairAction;
    }

    public FailureCause getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(FailureCause failureCause) {
        this.failureCause = failureCause;
    }

    public Double getUtility() {
        return utility;
    }

    public void setUtility(Double utility) {
        this.utility = utility;
    }
}
