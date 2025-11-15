package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPregunta {
    OPCION_MULTIPLE,
    DESARROLLO,
    VERDADERO_FALSO,
    ORDENAR,
    COMPLETAR;
    
    @JsonValue
    public String toValue() {
        return this.name();
    }
}

