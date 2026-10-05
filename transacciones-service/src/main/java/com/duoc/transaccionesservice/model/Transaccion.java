package com.duoc.transaccionesservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, length = 30)
    private String canal;

    @Column(nullable = false)
    private Boolean anomalia;

    protected Transaccion() {
    }

    public Long getId() {
        return id;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCanal() {
        return canal;
    }

    public Boolean getAnomalia() {
        return anomalia;
    }
}
