package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "prolongacion_ayuda")
public class ProlongacionAyuda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_prolongacion", nullable = false)
    private Date fechaProlongacion;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    public ProlongacionAyuda() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFechaProlongacion() {
        return fechaProlongacion;
    }

    public void setFechaProlongacion(Date fechaProlongacion) {
        this.fechaProlongacion = fechaProlongacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Usuario registradoPor) {
        this.registradoPor = registradoPor;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }
}
