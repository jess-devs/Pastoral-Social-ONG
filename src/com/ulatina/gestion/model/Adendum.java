package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adendum")
public class Adendum implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "observaciones")
    private String observaciones;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", unique = true, nullable = false)
    private Expediente expediente;

    @OneToMany(mappedBy = "adendum", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GastoMensual> gastosMensuales = new ArrayList<>();

    public Adendum() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }

    public List<GastoMensual> getGastosMensuales() { return gastosMensuales; }
    public void setGastosMensuales(List<GastoMensual> gastosMensuales) { this.gastosMensuales = gastosMensuales; }
}
