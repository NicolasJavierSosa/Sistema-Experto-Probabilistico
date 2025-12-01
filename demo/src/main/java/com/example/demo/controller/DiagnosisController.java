package com.example.demo.controller;

import com.example.demo.model.DiagnosisLog;
import com.example.demo.model.FailureCause;
import com.example.demo.model.RepairAction;
import com.example.demo.model.Symptom;
import com.example.demo.repository.DiagnosisLogRepository;
import com.example.demo.service.ExpertSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DiagnosisController {

    @Autowired
    private ExpertSystemService expertSystemService;

    @Autowired
    private DiagnosisLogRepository diagnosisLogRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Symptom> allSymptoms = expertSystemService.getAllSymptoms();
        
        // Group by category
        Map<String, List<Symptom>> symptomsByCategory = allSymptoms.stream()
                .collect(Collectors.groupingBy(s -> s.getCategory() != null ? s.getCategory() : "Otros"));
        
        model.addAttribute("symptomsByCategory", symptomsByCategory);
        return "index";
    }

    @PostMapping("/diagnose")
    public String diagnose(@RequestParam(required = false) List<Long> selectedSymptoms, Model model) {
        if (selectedSymptoms == null || selectedSymptoms.isEmpty()) {
            model.addAttribute("error", "Por favor seleccione al menos un síntoma.");
            
            // Re-populate model for view
            List<Symptom> allSymptoms = expertSystemService.getAllSymptoms();
            Map<String, List<Symptom>> symptomsByCategory = allSymptoms.stream()
                    .collect(Collectors.groupingBy(s -> s.getCategory() != null ? s.getCategory() : "Otros"));
            model.addAttribute("symptomsByCategory", symptomsByCategory);
            
            return "index";
        }

        // 1. Calculate Probabilities (Posteriors)
        Map<FailureCause, Double> posteriors = expertSystemService.calculateProbabilities(selectedSymptoms);
        
        // 2. Calculate Utilities
        Map<RepairAction, Double> utilities = expertSystemService.calculateExpectedUtilities(posteriors);
        
        // 3. Best Action
        RepairAction bestAction = utilities.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 4. Sensitivity Analysis
        List<String> sensitivity = expertSystemService.performSensitivityAnalysis(selectedSymptoms, posteriors, bestAction);

        // Save to DB
        // Fetch symptoms to log names
        List<Symptom> symptomsList = expertSystemService.getAllSymptoms().stream()
                .filter(s -> selectedSymptoms.contains(s.getId()))
                .collect(Collectors.toList());

        String symptomsString = symptomsList.stream()
                .map(Symptom::getName)
                .collect(Collectors.joining(", "));
        
        String actionName = bestAction != null ? bestAction.getName() : "Indeterminado";
        DiagnosisLog log = new DiagnosisLog(symptomsString, actionName);
        diagnosisLogRepository.save(log);

        // Add to model
        model.addAttribute("selectedSymptoms", symptomsList);
        model.addAttribute("posteriors", posteriors);
        model.addAttribute("utilities", utilities);
        model.addAttribute("bestAction", bestAction);
        model.addAttribute("sensitivity", sensitivity);

        return "result";
    }
}
