package com.example.demo.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

@Component
public class DataInitializer implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
        if (failureCauseRepository.count() > 0) {
            return; // Ya hay datos
        }
        actionOutcomeRepository.deleteAll();
        causalRelationRepository.deleteAll();
        symptomRepository.deleteAll();
        repairActionRepository.deleteAll();
        failureCauseRepository.deleteAll();

        // --- 1. CAUSAS DE FALLA (Failure Causes) ---
        // Priors ajustadas a desktop común, razonadas en base a datos de reparación y encuestas
        // (valores relativos; deben sumar ~1.0)
        FailureCause ramFail     = failureCauseRepository.save(new FailureCause("Falla de Memoria RAM",                    0.06)); // RAM falla poco
        FailureCause hddFail     = failureCauseRepository.save(new FailureCause("Falla de Disco Duro/SSD",                0.28)); // almacenamiento: causa frecuente de lentitud/boot
        FailureCause psuFail     = failureCauseRepository.save(new FailureCause("Falla de Fuente de Poder (PSU)",        0.06)); // poco frecuente
        FailureCause gpuFail     = failureCauseRepository.save(new FailureCause("Falla de Tarjeta Gráfica (GPU)",        0.12)); // sube si hay artefactos
        FailureCause moboFail    = failureCauseRepository.save(new FailureCause("Falla de Placa Madre",                   0.06)); // NIC/USB/variados
        FailureCause cpuOverheat = failureCauseRepository.save(new FailureCause("Sobrecalentamiento de CPU",              0.08)); // moderado
        FailureCause swCorrupt   = failureCauseRepository.save(new FailureCause("Corrupción de Sistema Operativo/Drivers",0.20)); // SO y drivers
        FailureCause malware     = failureCauseRepository.save(new FailureCause("Infección por Malware/Virus",           0.14)); // malware relativamente frecuente

        // --- 2. SÍNTOMAS (Symptoms) ---
        // Visual
        Symptom bsod = symptomRepository.save(new Symptom("Pantalla Azul (BSOD)", "Visual"));
        Symptom artifacts = symptomRepository.save(new Symptom("Artefactos/Rayas de colores", "Visual"));
        Symptom noVideo = symptomRepository.save(new Symptom("PC enciende pero no da video", "Visual"));
        Symptom freeze = symptomRepository.save(new Symptom("Imagen congelada", "Visual"));

        // Sonido
        Symptom beeps = symptomRepository.save(new Symptom("Pitidos (Beep codes) al arrancar", "Sonido"));
        Symptom loudFan = symptomRepository.save(new Symptom("Ventiladores a máxima velocidad/Ruidosos", "Sonido"));
        Symptom clickNoise = symptomRepository.save(new Symptom("Ruido mecánico (Click/Grind)", "Sonido"));
        Symptom noSound = symptomRepository.save(new Symptom("Sin audio en el sistema", "Sonido"));

        // Rendimiento
        Symptom slowBoot = symptomRepository.save(new Symptom("Inicio de sistema muy lento", "Rendimiento"));
        Symptom slowApp = symptomRepository.save(new Symptom("Lentitud al abrir programas", "Rendimiento"));
        Symptom lagGames = symptomRepository.save(new Symptom("Bajos FPS/Lag en juegos", "Rendimiento"));
        Symptom highCpu = symptomRepository.save(new Symptom("Uso de CPU al 100% constante", "Rendimiento"));

        // Energía / Estabilidad
        Symptom noPower = symptomRepository.save(new Symptom("No enciende nada (Muerto)", "Energía"));
        Symptom randomReboot = symptomRepository.save(new Symptom("Reinicios aleatorios repentinos", "Energía"));
        Symptom shutdownLoad = symptomRepository.save(new Symptom("Se apaga bajo carga (Juegos/Render)", "Energía"));
        Symptom bootLoop = symptomRepository.save(new Symptom("Bucle de reinicios (Boot loop)", "Energía"));

        // Otros
        Symptom popups = symptomRepository.save(new Symptom("Ventanas emergentes/Publicidad", "Software"));
        Symptom usbFail = symptomRepository.save(new Symptom("Puertos USB no responden", "Periféricos"));
        Symptom netFail = symptomRepository.save(new Symptom("Sin conexión a Internet/Red", "Red"));

        // --- 3. RELACIONES CAUSALES (P(Symptom | Cause)) ---
        // Ajustadas para que reflejen plausibilidad realista

        // RAM Failure (RAM causa sobre todo BSOD, freeze, reinicios)
        causalRelationRepository.save(new CausalRelation(ramFail, bsod, 0.80));
        causalRelationRepository.save(new CausalRelation(ramFail, beeps, 0.30));
        causalRelationRepository.save(new CausalRelation(ramFail, freeze, 0.70));
        causalRelationRepository.save(new CausalRelation(ramFail, randomReboot, 0.60));
        causalRelationRepository.save(new CausalRelation(ramFail, bootLoop, 0.45));

        // HDD/SSD Failure (fuertemente asociado a slowBoot, slowApp y clickNoise en HDD mecánico)
        causalRelationRepository.save(new CausalRelation(hddFail, slowBoot, 0.88));
        causalRelationRepository.save(new CausalRelation(hddFail, slowApp, 0.80));
        causalRelationRepository.save(new CausalRelation(hddFail, bsod, 0.30)); // errores de disco
        causalRelationRepository.save(new CausalRelation(hddFail, clickNoise, 0.65)); // mecánico
        causalRelationRepository.save(new CausalRelation(hddFail, freeze, 0.45));
        causalRelationRepository.save(new CausalRelation(hddFail, bootLoop, 0.25));

        // PSU Failure (power symptoms)
        causalRelationRepository.save(new CausalRelation(psuFail, noPower, 0.92));
        causalRelationRepository.save(new CausalRelation(psuFail, randomReboot, 0.75));
        causalRelationRepository.save(new CausalRelation(psuFail, shutdownLoad, 0.85));
        causalRelationRepository.save(new CausalRelation(psuFail, noVideo, 0.25));

        // GPU Failure (artefactos y noVideo principalmente)
        causalRelationRepository.save(new CausalRelation(gpuFail, artifacts, 0.92));
        causalRelationRepository.save(new CausalRelation(gpuFail, noVideo, 0.88));
        causalRelationRepository.save(new CausalRelation(gpuFail, bsod, 0.25)); // driver/hardware crash
        causalRelationRepository.save(new CausalRelation(gpuFail, lagGames, 0.80));
        causalRelationRepository.save(new CausalRelation(gpuFail, shutdownLoad, 0.35));

        // Motherboard Failure (diverso: USB, NIC, beeps, noPower)
        causalRelationRepository.save(new CausalRelation(moboFail, noPower, 0.65));
        causalRelationRepository.save(new CausalRelation(moboFail, beeps, 0.55));
        causalRelationRepository.save(new CausalRelation(moboFail, usbFail, 0.75));
        causalRelationRepository.save(new CausalRelation(moboFail, noVideo, 0.50));
        causalRelationRepository.save(new CausalRelation(moboFail, randomReboot, 0.40));
        causalRelationRepository.save(new CausalRelation(moboFail, netFail, 0.50)); // NIC

        // CPU Overheating (fan, shutdown under load, lag)
        causalRelationRepository.save(new CausalRelation(cpuOverheat, loudFan, 0.94));
        causalRelationRepository.save(new CausalRelation(cpuOverheat, shutdownLoad, 0.88));
        causalRelationRepository.save(new CausalRelation(cpuOverheat, lagGames, 0.80));
        causalRelationRepository.save(new CausalRelation(cpuOverheat, randomReboot, 0.50));

        // Software/Driver Corruption (BSOD, slowApp, freeze, net issues, drivers cause video artefacts too)
        causalRelationRepository.save(new CausalRelation(swCorrupt, bsod, 0.65));
        causalRelationRepository.save(new CausalRelation(swCorrupt, slowApp, 0.70));
        causalRelationRepository.save(new CausalRelation(swCorrupt, freeze, 0.50));
        causalRelationRepository.save(new CausalRelation(swCorrupt, netFail, 0.55));
        causalRelationRepository.save(new CausalRelation(swCorrupt, noSound, 0.60));
        causalRelationRepository.save(new CausalRelation(swCorrupt, usbFail, 0.45));
        causalRelationRepository.save(new CausalRelation(swCorrupt, noVideo, 0.40)); // driver crash/artifact causes

        // Malware (popups, high CPU, slow apps, networking problems)
        causalRelationRepository.save(new CausalRelation(malware, popups, 0.95));
        causalRelationRepository.save(new CausalRelation(malware, highCpu, 0.80));
        causalRelationRepository.save(new CausalRelation(malware, slowApp, 0.75));
        causalRelationRepository.save(new CausalRelation(malware, netFail, 0.50));
        causalRelationRepository.save(new CausalRelation(malware, usbFail, 0.20));

        // --- 4. ACCIONES DE REPARACIÓN (Repair Actions) ---
        // Cost representa USD aproximado + tiempo/labor realista
        RepairAction actReplaceRam = repairActionRepository.save(new RepairAction("Reemplazar Memoria RAM", 70.0));
        RepairAction actReplaceHdd = repairActionRepository.save(new RepairAction("Reemplazar Disco por SSD", 100.0));
        RepairAction actReplacePsu = repairActionRepository.save(new RepairAction("Reemplazar Fuente de Poder", 90.0));
        RepairAction actReplaceGpu = repairActionRepository.save(new RepairAction("Reemplazar Tarjeta Gráfica", 320.0));
        RepairAction actReplaceMobo = repairActionRepository.save(new RepairAction("Reemplazar Placa Madre", 180.0));
        RepairAction actCleanDust = repairActionRepository.save(new RepairAction("Limpieza Interna y Cambio Pasta Térmica", 40.0)); // algo más realista
        RepairAction actReinstallOs = repairActionRepository.save(new RepairAction("Formatear y Reinstalar Windows (incluye backup)", 120.0)); // mayor coste por tiempo/backup
        RepairAction actAntiMalware = repairActionRepository.save(new RepairAction("Ejecutar Limpieza de Malware", 50.0));
        RepairAction actUpdateDrivers = repairActionRepository.save(new RepairAction("Actualizar Drivers/BIOS", 25.0));

        // --- 5. UTILIDADES (Action Outcomes) ---
        // Valor conservador de un PC funcionando: valFixed (unidades monetarias/utilidad)
        double valFixed = 400.0; // menos inflado que antes, más coherente con costos

        // Definimos utilidades: si la acción arregla la causa -> valFixed - cost
        // si no la arregla -> -cost (pérdida de dinero por acción inútil)
        // Para algunas combinaciones admitimos "arreglo parcial" (ej. drivers pueden arreglar artefactos de driver pero no GPU físicamente muerta)

        // RAM
        createOutcome(actReplaceRam, ramFail, valFixed - actReplaceRam.getCost());
        createOutcome(actReplaceRam, moboFail, -actReplaceRam.getCost());

        // HDD
        createOutcome(actReplaceHdd, hddFail, valFixed - actReplaceHdd.getCost());
        // Reinstalar en nuevo SSD suele resolver problemas SW también (si SW relacionados al disco)
        createOutcome(actReplaceHdd, swCorrupt, valFixed - actReplaceHdd.getCost());

        // PSU
        createOutcome(actReplacePsu, psuFail, valFixed - actReplacePsu.getCost());
        createOutcome(actReplacePsu, moboFail, -actReplacePsu.getCost());

        // GPU
        createOutcome(actReplaceGpu, gpuFail, valFixed - actReplaceGpu.getCost());

        // Mobo
        createOutcome(actReplaceMobo, moboFail, valFixed - actReplaceMobo.getCost());
        createOutcome(actReplaceMobo, psuFail, -actReplaceMobo.getCost());

        // Limpieza / Pasta térmica
        createOutcome(actCleanDust, cpuOverheat, valFixed - actCleanDust.getCost());
        createOutcome(actCleanDust, psuFail, -actCleanDust.getCost());

        // Reinstall OS
        createOutcome(actReinstallOs, swCorrupt, valFixed - actReinstallOs.getCost());
        createOutcome(actReinstallOs, malware, valFixed - actReinstallOs.getCost()); // reinstalar suele eliminar muchas infecciones
        createOutcome(actReinstallOs, hddFail, -actReinstallOs.getCost()); // no arregla hardware

        // AntiMalware
        createOutcome(actAntiMalware, malware, valFixed - actAntiMalware.getCost());
        createOutcome(actAntiMalware, swCorrupt, -actAntiMalware.getCost()); // no arregla corrupción profunda

        // Update Drivers/BIOS
        // Puede arreglar corrupciones de drivers (alto valor) y en algunos casos solucionar artefactos causados por drivers.
        createOutcome(actUpdateDrivers, swCorrupt, valFixed - actUpdateDrivers.getCost());
        // Para artefactos: drivers pueden ayudar si el problema es software (parcial)
        // Modelamos reparación parcial: utilidad intermedia (ej: 60% del valor)
        double partialFixDriversForGpu = (valFixed * 0.60) - actUpdateDrivers.getCost(); // arreglo parcial si es driver-related
        createOutcome(actUpdateDrivers, gpuFail, partialFixDriversForGpu);
        // Si el problema es hardware GPU, updateDrivers no lo arregla y sólo cuesta
        // (por defecto si no hay outcome definido, el cálculo usará -cost)

    }

    private void createOutcome(RepairAction action, FailureCause cause, Double utility) {
        actionOutcomeRepository.save(new ActionOutcome(action, cause, utility));
    }
}
