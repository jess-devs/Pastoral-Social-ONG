package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.CondicionVivienda;
import com.ulatina.gestion.model.enums.TenenciaVivienda;
import com.ulatina.gestion.model.enums.TipoVivienda;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vivienda")
@NamedQueries({
        @NamedQuery(
                name = "Vivienda.findByExpediente",
                query = "SELECT v FROM Vivienda v WHERE v.expediente.id = :expedienteId"
        ),
        @NamedQuery(
                name = "Vivienda.findByTipo",
                query = "SELECT v FROM Vivienda v WHERE v.tipo = :tipo"
        ),
        @NamedQuery(
                name = "Vivienda.findByCondicion",
                query = "SELECT v FROM Vivienda v WHERE v.condicion = :condicion"
        )
})
public class Vivienda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "direccion")
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoVivienda tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenencia")
    private TenenciaVivienda tenencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicion")
    private CondicionVivienda condicion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", unique = true, nullable = false)
    private Expediente expediente;

    public Vivienda() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public TipoVivienda getTipo() { return tipo; }
    public void setTipo(TipoVivienda tipo) { this.tipo = tipo; }

    public TenenciaVivienda getTenencia() { return tenencia; }
    public void setTenencia(TenenciaVivienda tenencia) { this.tenencia = tenencia; }

    public CondicionVivienda getCondicion() { return condicion; }
    public void setCondicion(CondicionVivienda condicion) { this.condicion = condicion; }

    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
}
