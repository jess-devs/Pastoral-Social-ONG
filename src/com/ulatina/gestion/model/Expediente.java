package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.model.enums.EtapaExpediente;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "expediente")
@NamedQueries({
                @NamedQuery(name = "Expediente.findAll", query = "SELECT DISTINCT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "LEFT JOIN FETCH e.parroquia"),
                @NamedQuery(name = "Expediente.findByNumeroFicha", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "LEFT JOIN FETCH e.parroquia " +
                                "WHERE e.numeroFicha = :ficha"),
                @NamedQuery(name = "Expediente.findByParroquia", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "WHERE e.parroquia.id = :parroquiaId"),
                @NamedQuery(name = "Expediente.findByTitular", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "WHERE e.titular.id = :titularId"),
                @NamedQuery(name = "Expediente.findByEstado", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "WHERE e.estado = :estado"),
                @NamedQuery(name = "Expediente.findByEtapa", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "WHERE e.etapaActual = :etapa"),
                @NamedQuery(name = "Expediente.findByColorMarcador", query = "SELECT e FROM Expediente e " +
                                "LEFT JOIN FETCH e.titular " +
                                "WHERE e.colorMarcador = :color")
})
public class Expediente implements Serializable {

        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "numero_ficha", unique = true)
        private String numeroFicha;

        @Temporal(TemporalType.DATE)
        @Column(name = "fecha_inicio", nullable = false)
        private Date fechaInicio;

        @Temporal(TemporalType.DATE)
        @Column(name = "fecha_prevista_conclusion")
        private Date fechaPrevistaConclusion;

        @Temporal(TemporalType.DATE)
        @Column(name = "fecha_conclusion_real")
        private Date fechaConclusionReal;

        @Column(name = "entrevistador")
        private String entrevistador;

        @Lob
        @Column(name = "observaciones")
        private String observaciones;

        @Column(name = "color_marcador")
        private String colorMarcador;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado")
        private EstadoExpediente estado;

        @Enumerated(EnumType.STRING)
        @Column(name = "etapa_actual")
        private EtapaExpediente etapaActual;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "parroquia_id", nullable = false)
        private Parroquia parroquia;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "titular_id", nullable = false)
        private Persona titular;

        @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<MiembroFamiliar> miembrosFamiliares = new ArrayList<>();

        @OneToOne(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Vivienda vivienda;

        @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<DocumentoAdjunto> documentosAdjuntos = new ArrayList<>();

        @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<ProlongacionAyuda> prolongacionesAyuda = new ArrayList<>();

        @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<AsistenciaSolicitada> asistenciasSolicitadas = new ArrayList<>();

        @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<Entrevista> entrevistas = new ArrayList<>();

        @OneToOne(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Adendum adendum;

        public Expediente() {
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getNumeroFicha() {
                return numeroFicha;
        }

        public void setNumeroFicha(String numeroFicha) {
                this.numeroFicha = numeroFicha;
        }

        public Date getFechaInicio() {
                return fechaInicio;
        }

        public void setFechaInicio(Date fechaInicio) {
                this.fechaInicio = fechaInicio;
        }

        public Date getFechaPrevistaConclusion() {
                return fechaPrevistaConclusion;
        }

        public void setFechaPrevistaConclusion(Date fechaPrevistaConclusion) {
                this.fechaPrevistaConclusion = fechaPrevistaConclusion;
        }

        public Date getFechaConclusionReal() {
                return fechaConclusionReal;
        }

        public void setFechaConclusionReal(Date fechaConclusionReal) {
                this.fechaConclusionReal = fechaConclusionReal;
        }

        public String getEntrevistador() {
                return entrevistador;
        }

        public void setEntrevistador(String entrevistador) {
                this.entrevistador = entrevistador;
        }

        public String getObservaciones() {
                return observaciones;
        }

        public void setObservaciones(String observaciones) {
                this.observaciones = observaciones;
        }

        public String getColorMarcador() {
                return colorMarcador;
        }

        public void setColorMarcador(String colorMarcador) {
                this.colorMarcador = colorMarcador;
        }

        public EstadoExpediente getEstado() {
                return estado;
        }

        public void setEstado(EstadoExpediente estado) {
                this.estado = estado;
        }

        public EtapaExpediente getEtapaActual() {
                return etapaActual;
        }

        public void setEtapaActual(EtapaExpediente etapaActual) {
                this.etapaActual = etapaActual;
        }

        public Parroquia getParroquia() {
                return parroquia;
        }

        public void setParroquia(Parroquia parroquia) {
                this.parroquia = parroquia;
        }

        public Persona getTitular() {
                return titular;
        }

        public void setTitular(Persona titular) {
                this.titular = titular;
        }

        public List<MiembroFamiliar> getMiembrosFamiliares() {
                return miembrosFamiliares;
        }

        public void setMiembrosFamiliares(List<MiembroFamiliar> miembrosFamiliares) {
                this.miembrosFamiliares = miembrosFamiliares;
        }

        public Vivienda getVivienda() {
                return vivienda;
        }

        public void setVivienda(Vivienda vivienda) {
                this.vivienda = vivienda;
        }

        public List<DocumentoAdjunto> getDocumentosAdjuntos() {
                return documentosAdjuntos;
        }

        public void setDocumentosAdjuntos(List<DocumentoAdjunto> documentosAdjuntos) {
                this.documentosAdjuntos = documentosAdjuntos;
        }

        public List<ProlongacionAyuda> getProlongacionesAyuda() {
                return prolongacionesAyuda;
        }

        public void setProlongacionesAyuda(List<ProlongacionAyuda> prolongacionesAyuda) {
                this.prolongacionesAyuda = prolongacionesAyuda;
        }

        public List<AsistenciaSolicitada> getAsistenciasSolicitadas() {
                return asistenciasSolicitadas;
        }

        public void setAsistenciasSolicitadas(List<AsistenciaSolicitada> asistenciasSolicitadas) {
                this.asistenciasSolicitadas = asistenciasSolicitadas;
        }

        public List<Entrevista> getEntrevistas() {
                return entrevistas;
        }

        public void setEntrevistas(List<Entrevista> entrevistas) {
                this.entrevistas = entrevistas;
        }

        public Adendum getAdendum() {
                return adendum;
        }

        public void setAdendum(Adendum adendum) {
                this.adendum = adendum;
        }
}
