package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.TipoAsistencia;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@NamedQueries({
        @NamedQuery(name = "AsistenciaSolicitada.findByExpediente", query = "SELECT a FROM AsistenciaSolicitada a " +
                "WHERE a.expediente.id = :expedienteId " +
                "ORDER BY a.tipoAsistencia"),
        @NamedQuery(name = "AsistenciaSolicitada.findByTipo", query = "SELECT a FROM AsistenciaSolicitada a " +
                "WHERE a.tipoAsistencia = :tipo")
})
@Entity
@Table(name = "asistencia_solicitada")
public class AsistenciaSolicitada implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_asistencia")
    private TipoAsistencia tipoAsistencia;

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "frecuencia")
    private String frecuencia;

    @Column(name = "duracion")
    private String duracion;

    @Column(name = "valor", precision = 10, scale = 2)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    public AsistenciaSolicitada() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAsistencia getTipoAsistencia() {
        return tipoAsistencia;
    }

    public void setTipoAsistencia(TipoAsistencia tipoAsistencia) {
        this.tipoAsistencia = tipoAsistencia;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Expediente getExpediente() {
        return expediente;
    }

    public void setExpediente(Expediente expediente) {
        this.expediente = expediente;
    }
}
