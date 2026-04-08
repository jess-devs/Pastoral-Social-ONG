package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.EstadoCivil;
import com.ulatina.gestion.model.enums.Sexo;
import com.ulatina.gestion.model.enums.TipoDocumentoPersona;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "persona")
// Named Queries (JPQL)
@NamedQueries({
        @NamedQuery(
                name = "Persona.findByNumeroDocumento",
                query = "SELECT p FROM Persona p WHERE p.numeroDocumento = :num"
        ),
        @NamedQuery(
                name = "Persona.findByPaisOrigen",
                query = "SELECT p FROM Persona p WHERE p.paisOrigen = :pais"
        )
})
// Named Native Queries
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Persona.findByNombre",
                query = "SELECT * FROM persona " +
                        "WHERE LOWER(nombres) LIKE LOWER(:nombres) " +
                        "AND LOWER(apellidos) LIKE LOWER(:apellidos)",
                resultClass = Persona.class
        ),
        @NamedNativeQuery(
                name = "Persona.findByDireccion",
                query = "SELECT * FROM persona " +
                        "WHERE LOWER(direccion) LIKE LOWER(:direccion)",
                resultClass = Persona.class
        ),
        @NamedNativeQuery(
                name = "Persona.findByCondicionSalud",
                query = "SELECT * FROM persona " +
                        "WHERE LOWER(condicion_salud) LIKE LOWER(:condicion)",
                resultClass = Persona.class
        )
})
public class Persona implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumentoPersona tipoDocumento;

    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;

    @Column(name = "nacionalidad")
    private String nacionalidad;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo")
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_civil")
    private EstadoCivil estadoCivil;

    @Column(name = "profesion_oficio")
    private String profesionOficio;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "nivel_educacion")
    private String nivelEducacion;

    @Column(name = "condicion_salud")
    private String condicionSalud;

    @Column(name = "tiene_seguro")
    private Boolean tieneSeguro;

    @Column(name = "pais_origen")
    private String paisOrigen;

    @Column(name = "condicion_migratoria")
    private String condicionMigratoria;

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Expediente> expedientes = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MiembroFamiliar> miembrosFamiliares = new ArrayList<>();

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AsistenciaEvento> asistenciasEvento = new ArrayList<>();

    public Persona() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public TipoDocumentoPersona getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumentoPersona tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Sexo getSexo() { return sexo; }
    public void setSexo(Sexo sexo) { this.sexo = sexo; }

    public EstadoCivil getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(EstadoCivil estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getProfesionOficio() { return profesionOficio; }
    public void setProfesionOficio(String profesionOficio) { this.profesionOficio = profesionOficio; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNivelEducacion() { return nivelEducacion; }
    public void setNivelEducacion(String nivelEducacion) { this.nivelEducacion = nivelEducacion; }

    public String getCondicionSalud() { return condicionSalud; }
    public void setCondicionSalud(String condicionSalud) { this.condicionSalud = condicionSalud; }

    public Boolean getTieneSeguro() { return tieneSeguro; }
    public void setTieneSeguro(Boolean tieneSeguro) { this.tieneSeguro = tieneSeguro; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public String getCondicionMigratoria() { return condicionMigratoria; }
    public void setCondicionMigratoria(String condicionMigratoria) { this.condicionMigratoria = condicionMigratoria; }

    public List<Expediente> getExpedientes() { return expedientes; }
    public void setExpedientes(List<Expediente> expedientes) { this.expedientes = expedientes; }

    public List<MiembroFamiliar> getMiembrosFamiliares() { return miembrosFamiliares; }
    public void setMiembrosFamiliares(List<MiembroFamiliar> miembrosFamiliares) { this.miembrosFamiliares = miembrosFamiliares; }

    public List<AsistenciaEvento> getAsistenciasEvento() { return asistenciasEvento; }
    public void setAsistenciasEvento(List<AsistenciaEvento> asistenciasEvento) { this.asistenciasEvento = asistenciasEvento; }
}
