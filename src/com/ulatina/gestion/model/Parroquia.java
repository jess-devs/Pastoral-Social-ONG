package com.ulatina.gestion.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parroquia")
@NamedQueries({
        @NamedQuery(
                name = "Parroquia.findByNombre",
                query = "SELECT p FROM Parroquia p WHERE p.nombre = :nombre"
        ),
        @NamedQuery(
                name = "Parroquia.findActivas",
                query = "SELECT p FROM Parroquia p WHERE p.activa = true"
        )
})
public class Parroquia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "sector_filial")
    private String sectorFilial;

    @Column(name = "vicaria")
    private String vicaria;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "activa")
    private Boolean activa = true;

    @OneToMany(mappedBy = "parroquia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "parroquia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expediente> expedientes = new ArrayList<>();

    @OneToMany(mappedBy = "parroquia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evento> eventos = new ArrayList<>();

    public Parroquia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSectorFilial() { return sectorFilial; }
    public void setSectorFilial(String sectorFilial) { this.sectorFilial = sectorFilial; }

    public String getVicaria() { return vicaria; }
    public void setVicaria(String vicaria) { this.vicaria = vicaria; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public List<Expediente> getExpedientes() { return expedientes; }
    public void setExpedientes(List<Expediente> expedientes) { this.expedientes = expedientes; }

    public List<Evento> getEventos() { return eventos; }
    public void setEventos(List<Evento> eventos) { this.eventos = eventos; }
}
