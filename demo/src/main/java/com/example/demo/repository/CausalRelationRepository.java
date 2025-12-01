package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.CausalRelation;
import com.example.demo.model.FailureCause;

@Repository
public interface CausalRelationRepository extends JpaRepository<CausalRelation, Long> {
    List<CausalRelation> findByFailureCause(FailureCause failureCause);
}
