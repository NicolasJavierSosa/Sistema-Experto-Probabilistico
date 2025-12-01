# Sistema Experto de Diagnóstico de Hardware

Este proyecto implementa un Sistema Experto basado en Teoría de la Decisión para diagnosticar fallas de hardware en computadoras.

## Estructura del Proyecto

- **Modelos (`src/main/java/com/example/demo/model`):**
  - `Symptom`: Enumera los síntomas observables (Pantalla azul, No enciende, etc.).
  - `Diagnosis`: Enumera las causas posibles (Falla de RAM, PSU, GPU, etc.).
  - `Action`: Enumera las acciones de reparación posibles (Reemplazar RAM, PSU, etc.).

- **Lógica (`src/main/java/com/example/demo/service/ExpertSystemService.java`):**
  - Define el **Modelo Causal** y las **Probabilidades** (Priors y Likelihoods).
  - Implementa la **Inferencia Bayesiana** para calcular la probabilidad de cada diagnóstico dado los síntomas.
  - Calcula la **Utilidad Esperada** combinando probabilidades, costos de componentes y penalizaciones por falla.
  - Realiza el **Análisis de Sensibilidad** variando los costos para verificar la robustez de la decisión.

- **Interacción (`src/main/java/com/example/demo/runner/ConsoleInteractionRunner.java`):**
  - Interfaz de línea de comandos que guía al usuario paso a paso.

## Cómo Ejecutar

1. Asegúrate de tener Java y Maven instalados.
2. Ejecuta el comando:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Sigue las instrucciones en la consola para seleccionar los síntomas observados.

## Teoría Implementada

1. **Probabilidad Bayesiana:** $P(Diagnosis|Symptom) \propto P(Symptom|Diagnosis) \times P(Diagnosis)$
2. **Utilidad Esperada:** $EU(Action) = \sum P(Diagnosis|Evidence) \times Utility(Action, Diagnosis)$
3. **Análisis de Sensibilidad:** Se perturban los costos en un $\pm 20\%$ para observar cambios en la acción óptima.
