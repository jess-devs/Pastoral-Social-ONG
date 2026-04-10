package com.ulatina.gestion.model;

import com.ulatina.gestion.model.enums.TipoDocumentoAdjunto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NamedQueries({
    @NamedQuery(
        name = "DocumentoAdjunto.findByExpediente",
        query = "SELECT d FROM DocumentoAdjunto d LEFT JOIN FETCH d.subidoPor WHERE d.expediente.id = :expedienteId ORDER BY d.fechaSubida DESC"
    ),
    @NamedQuery(
        name = "DocumentoAdjunto.findFirmadosByExpediente",
        query = "SELECT d FROM DocumentoAdjunto d WHERE d.expediente.id = :expedienteId AND d.esDocFirmado = true"
    ),
    @NamedQuery(
        name = "DocumentoAdjunto.findByTipo",
        query = "SELECT d FROM DocumentoAdjunto d WHERE d.tipo = :tipo"
    )
})
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "DocumentoAdjunto.buscarPorDescripcion",
        query = "SELECT * FROM documento_adjunto WHERE expediente_id = :expedienteId AND LOWER(descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) ORDER BY fecha_subida DESC",
        resultClass = DocumentoAdjunto.class
    )
})
@Entity
@Table(name = "documento_adjunto")
public class DocumentoAdjunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoDocumentoAdjunto tipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "archivo_url")
    private String archivoUrl;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_subida")
    private Date fechaSubida;

    @Column(name = "es_doc_firmado")
    private Boolean esDocFirmado;

    @Column(name = "nombre_firmante")
    private String nombreFirmante;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_firma")
    private Date fechaFirma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subido_por")
    private Usuario subidoPor;

    public DocumentoAdjunto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoDocumentoAdjunto getTipo() { return tipo; }
    public void setTipo(TipoDocumentoAdjunto tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }

    public Date getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Date fechaSubida) { this.fechaSubida = fechaSubida; }

    public Boolean getEsDocFirmado() { return esDocFirmado; }
    public void setEsDocFirmado(Boolean esDocFirmado) { this.esDocFirmado = esDocFirmado; }

    public String getNombreFirmante() { return nombreFirmante; }
    public void setNombreFirmante(String nombreFirmante) { this.nombreFirmante = nombreFirmante; }

    public Date getFechaFirma() { return fechaFirma; }
    public void setFechaFirma(Date fechaFirma) { this.fechaFirma = fechaFirma; }

    public Expediente getExpediente() { return expediente; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }

    public Usuario getSubidoPor() { return subidoPor; }
    public void setSubidoPor(Usuario subidoPor) { this.subidoPor = subidoPor; }
}
