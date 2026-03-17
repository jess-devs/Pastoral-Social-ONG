package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.CategoriaGasto;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "gasto_mensual")
public class GastoMensual implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private CategoriaGasto categoria;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")
    private Date fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adendum_id", nullable = false)
    private Adendum adendum;

    public GastoMensual() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CategoriaGasto getCategoria() { return categoria; }
    public void setCategoria(CategoriaGasto categoria) { this.categoria = categoria; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Adendum getAdendum() { return adendum; }
    public void setAdendum(Adendum adendum) { this.adendum = adendum; }
}
