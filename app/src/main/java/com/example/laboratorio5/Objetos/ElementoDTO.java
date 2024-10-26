package com.example.laboratorio5.Objetos;

import java.io.Serializable;
import java.math.BigDecimal;

public class ElementoDTO implements Serializable {
    private String nombre;
    private BigDecimal calorias;

    public BigDecimal getCalorias() {
        return calorias;
    }

    public void setCalorias(BigDecimal calorias) {
        this.calorias = calorias;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
