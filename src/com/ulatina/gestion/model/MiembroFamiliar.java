package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "miembro_familiar")
public class MiembroFamiliar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "es_jefatura")
    private Boolean esJefatura;

    @Column(name = "relacion_titular")
    private String relacionTitular;

    @Column(name = "ocupacion")
    private String ocupacion;

    @Column(name = "trabaja")
    private Boolean trabaja;

    @Column(name = "ingreso_mensual", precision = 10, scale = 2)
    private BigDecimal ingresoMensual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    public MiembroFamiliar() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getEsJefatura() { return esJefatura; }
    public void setEsJefatura(Boolean esJefatura) { this.esJefatura = esJefatura; }

    public String getRelacionTitular() { return relacionTitular; }
    public void setRelacionTitular(String relacionTitular) { this.relacionTitular = relacionTitular; }

    public String getOcupacion() { return ocupacion; }
    public void setOcupacion(String ocupacion) { this.ocupacion = ocupacion; }

    public Boolean getTrabaja() { return trabaja; }
    public void setTrabaja(Boolean trabaja) { this.trabaja = trabaja; }

    public BigDecimal getIngresoMensual() { return ingresoMensual; }
    public void setIngresoMensual(BigDecimal ingresoMensual) { this.ingresoMensual = ingresoMensual; }

    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
}
