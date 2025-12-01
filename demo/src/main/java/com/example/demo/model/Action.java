package com.example.demo.model;

public enum Action {
    REPLACE_PSU("Reemplazar Fuente de Poder"),
    REPLACE_RAM("Reemplazar Memoria RAM"),
    REPLACE_GPU("Reemplazar Tarjeta Gráfica"),
    REPLACE_MOTHERBOARD("Reemplazar Placa Madre"),
    REPLACE_HDD("Reemplazar Disco Duro"),
    DIAGNOSTIC_CHECK("Realizar Diagnóstico General (Sin cambios)");

    private final String description;

    Action(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
