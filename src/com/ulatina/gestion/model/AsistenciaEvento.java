package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "asistencia_evento")
public class AsistenciaEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "asistio")
    private Boolean asistio;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    public AsistenciaEvento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getAsistio() { return asistio; }
    public void setAsistio(Boolean asistio) { this.asistio = asistio; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }
}
