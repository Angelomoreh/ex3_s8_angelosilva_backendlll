package com.duoc.cuentasservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cuentas")
public class Cuenta {

    @Id
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false)
    private Boolean activa;

    @Column(nullable = false, length = 3)
    private String moneda;

    @Version
    @Column(nullable = false)
    private Long version;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    public Cuenta() {
    }

    @PreUpdate
    void actualizarFecha() {
        fechaActualizacion = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getTipo() {
        return tipo;
    }

    public Boolean getActiva() {
        return activa;
    }

    public String getMoneda() {
        return moneda;
    }

    public OffsetDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
