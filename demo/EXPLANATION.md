# Sistema Experto Probabilístico de Diagnóstico de Hardware

Este documento explica el funcionamiento teórico y práctico del Sistema Experto implementado.

## 1. Introducción

Este sistema es un **Sistema de Soporte a la Decisión (DSS)** diseñado para diagnosticar fallas en computadoras personales. A diferencia de los sistemas expertos tradicionales basados en reglas rígidas ("SI A ENTONCES B"), este sistema utiliza un enfoque **probabilístico** basado en Redes Bayesianas y Teoría de la Utilidad.

Esto permite al sistema:
*   Manejar la **incertidumbre** (ej. un síntoma puede ser causado por múltiples fallas).
*   Recomendar acciones basadas en **costo-beneficio** (ej. probar primero la solución más barata si las probabilidades son similares).
*   Adaptarse a información incompleta.

## 2. Modelo Probabilístico (Red Bayesiana Naive)

El núcleo del diagnóstico se basa en el Teorema de Bayes.

### 2.1. Probabilidades A Priori $P(Causa)$
Representan la frecuencia estadística de una falla en la población general de computadoras, antes de observar cualquier síntoma.
*   *Ejemplo:* Es más probable que falle un Disco Duro ($P=0.20$) a que falle una Placa Madre ($P=0.05$).

### 2.2. Probabilidades Condicionales $P(Síntoma | Causa)$
Representan la "fuerza" de la relación causal. ¿Qué tan probable es que aparezca el síntoma X si la causa Y es verdadera?
*   *Ejemplo:* Si falla la RAM, es muy probable ver una Pantalla Azul ($P=0.85$). Si falla el Disco Duro, es menos probable ($P=0.40$).

### 2.3. Inferencia (Cálculo de la Probabilidad Posterior)
Cuando el usuario selecciona síntomas ($S_1, S_2...$), el sistema calcula la probabilidad de cada causa ($C$) usando la regla de Bayes (simplificada bajo asunción de independencia condicional "Naive"):

$$ P(C | S_1, S_2) \propto P(C) \times \prod_{i} P(S_i | C) $$

El resultado se normaliza para que la suma de probabilidades de todas las causas sea 100%.

## 3. Teoría de la Decisión (Utilidad Esperada)

Saber qué está roto no es suficiente; el usuario necesita saber **qué hacer**. El sistema calcula la **Utilidad Esperada (EU)** de cada acción de reparación.

$$ EU(Acción) = \sum_{Causas} P(Causa | Síntomas) \times Utilidad(Acción, Causa) $$

Donde la **Utilidad** se define como:
*   **Si la acción arregla la falla:** $ValorPC - CostoAcción$
*   **Si la acción NO arregla la falla:** $-CostoAcción$ (Pérdida neta)

Esto significa que el sistema podría recomendar una acción barata (ej. "Actualizar Drivers") incluso si la probabilidad de que sea software es menor que la de hardware, simplemente porque el riesgo de perder dinero es bajo.

## 4. Análisis de Sensibilidad

El sistema verifica la robustez de su recomendación.
1.  Toma la causa más probable.
2.  Varía artificialmente su probabilidad a priori (simulando que nuestra estadística base estaba mal).
3.  Recalcula la mejor acción.
4.  Si la decisión cambia drásticamente con un cambio pequeño en la probabilidad, el sistema advierte al usuario que el diagnóstico es "sensible" o incierto.

## 5. Base de Conocimientos Actual

El sistema cuenta con una base de datos inicializada con:
*   **8 Causas de Falla:** RAM, HDD/SSD, PSU, GPU, Motherboard, CPU Overheat, Software, Malware.
*   **16 Síntomas:** Categorizados en Visual, Sonido, Rendimiento, Energía, etc.
*   **9 Acciones de Reparación:** Desde limpieza simple hasta reemplazo de componentes costosos.
*   **Reglas:** Decenas de relaciones causales con probabilidades calibradas para realismo.

## 6. Tecnologías

*   **Backend:** Java 21, Spring Boot 3.
*   **Base de Datos:** PostgreSQL (Persistencia de reglas y logs).
*   **Frontend:** Thymeleaf (Renderizado lado servidor).
*   **Matemáticas:** Implementación propia de inferencia Bayesiana.
