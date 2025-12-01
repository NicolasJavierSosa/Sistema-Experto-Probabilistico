package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ActionOutcome;
import com.example.demo.model.RepairAction;

@Repository
public interface ActionOutcomeRepository extends JpaRepository<ActionOutcome, Long> {
    List<ActionOutcome> findByRepairAction(RepairAction repairAction);
}
