package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.TipoEvento;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "evento")
@NamedQueries({
        @NamedQuery(
                name = "Evento.findAll",
                query = "SELECT DISTINCT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia"
        ),
        @NamedQuery(
                name = "Evento.findById",
                query = "SELECT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia " +
                        "WHERE e.id = :id"
        ),
        @NamedQuery(
                name = "Evento.findByParroquia",
                query = "SELECT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia " +
                        "WHERE e.parroquia.id = :parroquiaId"
        ),
        @NamedQuery(
                name = "Evento.findByTipo",
                query = "SELECT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia " +
                        "WHERE e.tipo = :tipo"
        ),
        @NamedQuery(
                name = "Evento.findByFecha",
                query = "SELECT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia " +
                        "WHERE e.fecha = :fecha"
        ),
        @NamedQuery(
                name = "Evento.findByNombre",
                query = "SELECT e FROM Evento e " +
                        "LEFT JOIN FETCH e.parroquia " +
                        "WHERE e.nombre = :nombre"
        )
})
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @Temporal(TemporalType.TIME)
    @Column(name = "hora")
    private Date hora;

    @Column(name = "lugar")
    private String lugar;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoEvento tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parroquia_id", nullable = false)
    private Parroquia parroquia;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AsistenciaEvento> asistencias = new ArrayList<>();

    public Evento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Date getHora() { return hora; }
    public void setHora(Date hora) { this.hora = hora; }

    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public TipoEvento getTipo() { return tipo; }
    public void setTipo(TipoEvento tipo) { this.tipo = tipo; }

    public Parroquia getParroquia() { return parroquia; }
    public void setParroquia(Parroquia parroquia) { this.parroquia = parroquia; }

    public List<AsistenciaEvento> getAsistencias() { return asistencias; }
    public void setAsistencias(List<AsistenciaEvento> asistencias) { this.asistencias = asistencias; }
}