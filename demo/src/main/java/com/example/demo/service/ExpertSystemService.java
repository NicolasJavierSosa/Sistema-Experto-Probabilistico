package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.ActionOutcome;
import com.example.demo.model.CausalRelation;
import com.example.demo.model.FailureCause;
import com.example.demo.model.RepairAction;
import com.example.demo.model.Symptom;
import com.example.demo.repository.ActionOutcomeRepository;
import com.example.demo.repository.CausalRelationRepository;
import com.example.demo.repository.FailureCauseRepository;
import com.example.demo.repository.RepairActionRepository;
import com.example.demo.repository.SymptomRepository;

@Service
public class ExpertSystemService {

    @Autowired
    private FailureCauseRepository failureCauseRepository;
    @Autowired
    private SymptomRepository symptomRepository;
    @Autowired
    private CausalRelationRepository causalRelationRepository;
    @Autowired
    private RepairActionRepository repairActionRepository;
    @Autowired
    private ActionOutcomeRepository actionOutcomeRepository;

    public List<Symptom> getAllSymptoms() {
        return symptomRepository.findAll();
    }

    // Calculate P(Failure | Symptoms) using Naive Bayes
    public Map<FailureCause, Double> calculateProbabilities(List<Long> observedSymptomIds) {
        List<FailureCause> causes = failureCauseRepository.findAll();
        Map<FailureCause, Double> unnormalizedProbs = new HashMap<>();
        double normalizationConstant = 0.0;

        for (FailureCause cause : causes) {
            double prob = cause.getPriorProbability();
            List<CausalRelation> relations = causalRelationRepository.findByFailureCause(cause);

            // Map symptom ID to probability P(S|F) for this cause
            Map<Long, Double> symptomProbs = relations.stream()
                    .collect(Collectors.toMap(r -> r.getSymptom().getId(), CausalRelation::getProbability));

            for (Long symptomId : observedSymptomIds) {
                // If the cause produces the symptom, multiply by P(S|F)
                // If not explicitly defined, assume a small epsilon probability (e.g., 0.01) or
                // 0?
                // For robustness, let's assume if not defined it's very unlikely but not
                // impossible, say 0.01
                // Or strictly, if not in DB, P(S|F) = 0.
                // Let's use 0.001 for "unrelated" to avoid zeroing out everything.
                Double p_s_given_f = symptomProbs.getOrDefault(symptomId, 0.001);
                prob *= p_s_given_f;
            }

            unnormalizedProbs.put(cause, prob);
            normalizationConstant += prob;
        }

        // Normalize
        Map<FailureCause, Double> normalizedProbs = new HashMap<>();
        if (normalizationConstant > 0) {
            for (Map.Entry<FailureCause, Double> entry : unnormalizedProbs.entrySet()) {
                normalizedProbs.put(entry.getKey(), entry.getValue() / normalizationConstant);
            }
        } else {
            // Fallback if all probs are 0 (shouldn't happen with epsilon)
            for (FailureCause cause : causes) {
                normalizedProbs.put(cause, 1.0 / causes.size());
            }
        }
        return normalizedProbs;
    }

    // Calculate EU(Action) = Sum(P(F) * U(A, F))
    public Map<RepairAction, Double> calculateExpectedUtilities(Map<FailureCause, Double> failureProbabilities) {
        List<RepairAction> actions = repairActionRepository.findAll();
        Map<RepairAction, Double> expectedUtilities = new HashMap<>();

        for (RepairAction action : actions) {
            double eu = 0.0;
            List<ActionOutcome> outcomes = actionOutcomeRepository.findByRepairAction(action);
            Map<Long, Double> outcomeUtilities = outcomes.stream()
                    .collect(Collectors.toMap(o -> o.getFailureCause().getId(), ActionOutcome::getUtility));

            for (Map.Entry<FailureCause, Double> entry : failureProbabilities.entrySet()) {
                FailureCause cause = entry.getKey();
                Double p_f = entry.getValue();
                // Default utility if not defined (e.g., cost of action only, assuming it
                // doesn't fix it)
                // U = -Cost. If it fixes it, U is higher.
                // Let's assume if no outcome defined, it's just the cost (negative utility).
                Double u_a_f = outcomeUtilities.getOrDefault(cause.getId(), -action.getCost());
                eu += p_f * u_a_f;
            }
            expectedUtilities.put(action, eu);
        }
        return expectedUtilities;
    }

    // Sensitivity Analysis: Vary the prior of the top cause and see if decision
    // changes
    public List<String> performSensitivityAnalysis(List<Long> observedSymptomIds,
            Map<FailureCause, Double> currentProbs, RepairAction bestAction) {
        List<String> analysisResults = new ArrayList<>();

        // Find top cause
        FailureCause topCause = currentProbs.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (topCause == null)
            return analysisResults;

        analysisResults.add("Analizando sensibilidad para la causa más probable: " + topCause.getName());

        // Vary prior from 0.1 to 0.9
        for (double p = 0.1; p <= 0.9; p += 0.2) {
            // Create a temporary modified prior set (simplification: just update the top
            // cause's prior in calculation logic would be complex without refactoring)
            // For this demo, we will simulate it by manually adjusting the *posterior* or
            // re-running calc with modified object?
            // Re-running calc is safer. We need to temporarily change the entity in memory
            // (not save to DB).

            double originalPrior = topCause.getPriorProbability();
            topCause.setPriorProbability(p); // Modify in memory

            // Re-calculate
            Map<FailureCause, Double> newProbs = calculateProbabilities(observedSymptomIds);
            Map<RepairAction, Double> newUtilities = calculateExpectedUtilities(newProbs);

            RepairAction newBestAction = newUtilities.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            String status = (newBestAction != null && newBestAction.getId().equals(bestAction.getId()))
                    ? "MISMA decisión"
                    : "CAMBIO de decisión a " + newBestAction.getName();
            analysisResults.add(String.format("Si P(%s) fuera %.1f -> %s", topCause.getName(), p, status));

            topCause.setPriorProbability(originalPrior); // Restore
        }

        return analysisResults;
    }
}
