package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "entrevista")
public class Entrevista implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Column(name = "entrevistador")
    private String entrevistador;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "recomienda_ayuda")
    private Boolean recomiendaAyuda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    public Entrevista() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getEntrevistador() { return entrevistador; }
    public void setEntrevistador(String entrevistador) { this.entrevistador = entrevistador; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getRecomiendaAyuda() { return recomiendaAyuda; }
    public void setRecomiendaAyuda(Boolean recomiendaAyuda) { this.recomiendaAyuda = recomiendaAyuda; }

    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
}
