package com.example.demo.model;

public enum Diagnosis {
    PSU_FAILURE("Falla en Fuente de Poder"),
    RAM_FAILURE("Falla en Memoria RAM"),
    GPU_FAILURE("Falla en Tarjeta Gráfica"),
    MOTHERBOARD_FAILURE("Falla en Placa Madre"),
    HDD_FAILURE("Falla en Disco Duro"),
    NO_ISSUE("Sin Problemas Detectados");

    private final String description;

    Diagnosis(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
