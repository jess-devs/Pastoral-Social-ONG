package com.ulatina.gestion.controller;

import com.ulatina.gestion.dao.*;
import com.ulatina.gestion.dao.impl.*;
import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.EstadoExpediente;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

/**
 * Controlador principal de expedientes.
 * Concentra todos los accesos a DAOs relacionados con el expediente,
 * liberando a los formularios de instanciar DAOs directamente.
 */
public class ExpedienteController {

    private final IExpedienteDAO expedienteDAO = new ExpedienteDAOImpl();
    private final IPersonaDAO personaDAO = new PersonaDAOImpl();
    private final IViviendaDAO viviendaDAO = new ViviendaDAOImpl();
    private final IAdendumDAO adendumDAO = new AdendumDAOImpl();
    private final IGastoMensualDAO gastoDAO = new GastoMensualDAOImpl();
    private final IMiembroFamiliarDAO miembroDAO = new MiembroFamiliarDAOImpl();
    private final IDocumentoAdjuntoDAO documentoDAO = new DocumentoAdjuntoDAOImpl();
    private final IAsistenciaSolicitadaDAO asistenciaDAO = new AsistenciaSolicitadaDAOImpl();
    private final IEntrevistaDAO entrevistaDAO = new EntrevistaDAOImpl();
    private final IProlongacionAyudaDAO prolongacionDAO = new ProlongacionAyudaDAOImpl();

    // ═════════════════════════════════════════════════════════════════════════
    // CONSULTAS — Expediente
    // ═════════════════════════════════════════════════════════════════════════

    public List<Expediente> findAll() {
        try {
            return expedienteDAO.findAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Expediente findByNumeroFicha(String ficha) {
        try {
            return expedienteDAO.findByNumeroFicha(ficha);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** Métricas para el Dashboard. */
    public DashboardMetrics getMetrics() {
        List<Expediente> todos = findAll();
        long activos = todos.stream().filter(e -> EstadoExpediente.ACTIVO.equals(e.getEstado())).count();
        long enProceso = todos.stream().filter(e -> EstadoExpediente.EN_PROCESO.equals(e.getEstado())).count();
        long cerrados = todos.stream().filter(e -> EstadoExpediente.CERRADO.equals(e.getEstado())).count();
        return new DashboardMetrics(todos.size(), activos, enProceso, cerrados);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // CONSULTAS — Sub-entidades
    // ═════════════════════════════════════════════════════════════════════════

    public Vivienda findViviendaByExpediente(Long expedienteId) {
        try {
            return viviendaDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Adendum findAdendumByExpediente(Long expedienteId) {
        try {
            return adendumDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<MiembroFamiliar> findMiembrosByExpediente(Long expedienteId) {
        try {
            return miembroDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<DocumentoAdjunto> findDocsByExpediente(Long expedienteId) {
        try {
            return documentoDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<AsistenciaSolicitada> findAsistenciasByExpediente(Long expedienteId) {
        try {
            return asistenciaDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Entrevista> findEntrevistasByExpediente(Long expedienteId) {
        try {
            return entrevistaDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<ProlongacionAyuda> findProlongacionesByExpediente(Long expedienteId) {
        try {
            return prolongacionDAO.findByExpediente(expedienteId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<GastoMensual> findGastosByAdendum(Long adendumId) {
        try {
            return gastoDAO.findByAdendum(adendumId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Persona findPersonaByNumeroDocumento(String numDoc) {
        try {
            return personaDAO.findByNumeroDocumento(numDoc);
        } catch (Exception ex) {
            return null;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // ESCRITURA
    // ═════════════════════════════════════════════════════════════════════════

    public void guardarPersona(Persona p) {
        if (p.getId() == null)
            personaDAO.save(p);
        else
            personaDAO.update(p);
    }

    public void guardarExpediente(Expediente exp) {
        if (exp.getId() == null)
            expedienteDAO.save(exp);
        else
            expedienteDAO.update(exp);
    }

    /**
     * RF-13: Guarda Persona y Expediente en una sola transacción JPA.
     * Si cualquiera de las dos falla, se hace rollback completo.
     */
    public void guardarTitularYExpediente(Persona titular, Expediente expediente) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            if (titular.getId() == null)
                em.persist(titular);
            else
                em.merge(titular);
            em.flush(); // asegura que titular.id esté disponible antes de continuar

            expediente.setTitular(titular);
            if (expediente.getId() == null)
                em.persist(expediente);
            else
                em.merge(expediente);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new RuntimeException("Error al guardar titular y expediente: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void guardarVivienda(Vivienda v) {
        if (v.getId() == null)
            viviendaDAO.save(v);
        else
            viviendaDAO.update(v);
    }

    public void guardarAdendum(Adendum a) {
        if (a.getId() == null)
            adendumDAO.save(a);
        else
            adendumDAO.update(a);
    }

    public void guardarGasto(GastoMensual g) {
        if (g.getId() == null)
            gastoDAO.save(g);
        else
            gastoDAO.update(g);
    }

    public void eliminarExpediente(Long id) {
        expedienteDAO.delete(id);
    }

    public void eliminarGasto(Long id) {
        gastoDAO.delete(id);
    }

    public void guardarMiembro(MiembroFamiliar m) {
        if (m.getId() == null) miembroDAO.save(m);
        else miembroDAO.update(m);
    }

    public void eliminarMiembro(Long id) {
        miembroDAO.delete(id);
    }

    public void guardarDocumento(DocumentoAdjunto d) {
        if (d.getId() == null) documentoDAO.save(d);
        else documentoDAO.update(d);
    }

    public void eliminarDocumento(Long id) {
        documentoDAO.delete(id);
    }

    public void guardarAsistencia(AsistenciaSolicitada a) {
        if (a.getId() == null) asistenciaDAO.save(a);
        else asistenciaDAO.update(a);
    }

    public void eliminarAsistencia(Long id) {
        asistenciaDAO.delete(id);
    }

    public void guardarEntrevista(Entrevista e) {
        if (e.getId() == null) entrevistaDAO.save(e);
        else entrevistaDAO.update(e);
    }

    public void guardarProlongacion(ProlongacionAyuda p) {
        if (p.getId() == null) prolongacionDAO.save(p);
        else prolongacionDAO.update(p);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // UTILITARIO
    // ═════════════════════════════════════════════════════════════════════════

    /** Genera el número de ficha a partir del número de documento del titular. */
    public String generarNumeroFicha(String numeroDocumento) {
        return "EXP-" + numeroDocumento;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // DTO interno
    // ═════════════════════════════════════════════════════════════════════════

    public static class DashboardMetrics {
        public final int total;
        public final long activos;
        public final long enProceso;
        public final long cerrados;

        public DashboardMetrics(int total, long activos, long enProceso, long cerrados) {
            this.total = total;
            this.activos = activos;
            this.enProceso = enProceso;
            this.cerrados = cerrados;
        }
    }
}
