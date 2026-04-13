package com.ulatina.gestion.test;

import com.ulatina.gestion.model.*;
import com.ulatina.gestion.model.enums.*;
import com.ulatina.gestion.util.JPAUtil;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * Genera datos de prueba realistas (Costa Rica) para validar la app bajo carga.
 * Ejecutar directamente con main(). Requiere persistence.xml configurado.
 *
 * Produce:
 *   - 3 parroquias
 *   - 5 usuarios (roles variados)
 *   - 80+ expedientes distribuidos por parroquia y etapa
 *   - Incluye expedientes "mega" con 25+ registros por categoría para probar paginación
 */
public class DataSeeder {

    // ── datos base ──────────────────────────────────────────────────────────

    private static final String[] NOMBRES_M = {
        "Carlos", "José", "Luis", "Miguel", "Andrés", "Juan", "Diego", "Roberto",
        "Francisco", "Alejandro", "Eduardo", "Sergio", "Ricardo", "Mauricio", "Óscar",
        "Héctor", "Rodrigo", "Fernando", "Gustavo", "Pablo"
    };
    private static final String[] NOMBRES_F = {
        "María", "Ana", "Laura", "Sofía", "Andrea", "Valeria", "Daniela", "Carolina",
        "Patricia", "Gabriela", "Silvia", "Marcela", "Carmen", "Rosa", "Elena",
        "Lucía", "Isabel", "Verónica", "Mónica", "Alejandra"
    };
    private static final String[] APELLIDOS = {
        "González", "Rodríguez", "Vargas", "Jiménez", "Solís", "Mora", "Castro",
        "Chacón", "Rojas", "Quesada", "Vega", "Arce", "Brenes", "Calderón",
        "Herrera", "Monge", "Pérez", "Ulate", "Acosta", "Loría"
    };
    private static final String[] BARRIOS_CR = {
        "Hatillo Centro", "Hatillo 3", "Hatillo 6", "Pavas", "La Carpio",
        "Desamparados Centro", "San Antonio de Desamparados", "Alajuelita",
        "Aserrí", "Curridabat", "San José Centro", "La Uruca",
        "Barrio México", "Barrio Cuba", "Sagrada Familia",
        "Los Guido", "Patarrá", "San Rafael Abajo", "Tres Ríos"
    };
    private static final String[] PROFESIONES = {
        "Vendedor ambulante", "Empleada doméstica", "Agricultor", "Obrero construcción",
        "Operaria de zona franca", "Costurera", "Mecánico", "Conductor de bus",
        "Peón agrícola", "Lavaplatos", "Mesero", "Vigilante", "Recolector de basura",
        "Reciclador informal", "Operador de bodega", "Desempleado", "Pensionado",
        "Artesano", "Vendedora de lotería", "Ayudante de cocina"
    };
    private static final String[] CONDICIONES_SALUD = {
        "Diabetes tipo 2", "Hipertensión arterial", "Asma", "Artritis",
        "Discapacidad motora", "Ninguna", "Insuficiencia renal", "Enfermedad cardíaca",
        "VIH tratado", "Depresión", "Anemia crónica", "EPOC"
    };
    private static final String[] NACIONALIDADES_CR = {
        "Costarricense", "Nicaragüense", "Venezolana", "Colombiana",
        "Hondureña", "Panameña", "Peruana", "Salvadoreña"
    };
    private static final String[] ENTREVISTADORES = {
        "Hna. María Eugenia Ulate", "Pbro. Carlos Morales", "Lic. Ana Vargas",
        "Sr. Roberto Jiménez", "Sra. Sofía Chacón"
    };
    private static final String[] RELACIONES_FAMILIAR = {
        "Esposo/a", "Hijo/a", "Madre", "Padre", "Hermano/a",
        "Abuelo/a", "Nieto/a", "Tío/a", "Cuñado/a", "Sobrino/a"
    };

    // ── utilidades aleatorias ────────────────────────────────────────────────

    private static final Random RND = new Random(42);

    private static <T> T pick(T[] arr) {
        return arr[RND.nextInt(arr.length)];
    }

    private static <T> T pick(List<T> list) {
        return list.get(RND.nextInt(list.size()));
    }

    private static Date fechaHaceAnyos(int min, int max) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -(min + RND.nextInt(max - min)));
        c.add(Calendar.DAY_OF_YEAR, -RND.nextInt(365));
        return c.getTime();
    }

    private static Date fechaHaceDias(int min, int max) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -(min + RND.nextInt(max - min)));
        return c.getTime();
    }

    private static Date fechaEnDias(int min, int max) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, (min + RND.nextInt(max - min)));
        return c.getTime();
    }

    /** Genera un número de cédula CR simulado: 1-1234-5678 */
    private static String generarCedula(int contador) {
        int provincia = 1 + (contador % 7);
        int tomo = 1000 + contador;
        int asiento = 100 + (contador * 3 % 900);
        return provincia + "-" + tomo + "-" + String.format("%04d", asiento);
    }

    private static String generarNumeroFicha(String numDoc) {
        return "EXP-" + numDoc;
    }

    private static BigDecimal monto(int min, int max) {
        return BigDecimal.valueOf(min + RND.nextInt(max - min));
    }

    // ── construcción de entidades ───────────────────────────────────────────

    private static Parroquia crearParroquia(String nombre, String vicaria,
                                            String sector, String dir, String tel) {
        Parroquia p = new Parroquia();
        p.setNombre(nombre);
        p.setVicaria(vicaria);
        p.setSectorFilial(sector);
        p.setDireccion(dir);
        p.setTelefono(tel);
        p.setActiva(true);
        return p;
    }

    private static Usuario crearUsuario(String nombre, String email,
                                        RolUsuario rol, Parroquia parroquia) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPasswordHash("$2a$12$hashedpassword" + rol.name());
        u.setRol(rol);
        u.setActivo(true);
        u.setParroquia(parroquia);
        return u;
    }

    private static Persona crearPersona(int idx, Sexo sexo) {
        boolean esMasc = sexo == Sexo.MASCULINO;
        String nombre = pick(esMasc ? NOMBRES_M : NOMBRES_F);
        String apellido1 = pick(APELLIDOS);
        String apellido2 = pick(APELLIDOS);

        Persona p = new Persona();
        p.setNombres(nombre);
        p.setApellidos(apellido1 + " " + apellido2);
        p.setTipoDocumento(TipoDocumentoPersona.CEDULA);
        p.setNumeroDocumento(generarCedula(idx));
        p.setNacionalidad(pick(NACIONALIDADES_CR));
        p.setPaisOrigen(RND.nextInt(4) == 0 ? "Nicaragua" : "Costa Rica");
        p.setFechaNacimiento(fechaHaceAnyos(20, 70));
        p.setSexo(sexo);
        p.setEstadoCivil(pick(EstadoCivil.values()));
        p.setProfesionOficio(pick(PROFESIONES));
        p.setDireccion(pick(BARRIOS_CR) + ", San José");
        p.setTelefono("8" + String.format("%07d", 1000000 + idx));
        p.setNivelEducacion(pick(new String[]{"Primaria incompleta", "Primaria completa",
                "Secundaria incompleta", "Secundaria completa", "Técnico", "Universitario"}));
        p.setCondicionSalud(pick(CONDICIONES_SALUD));
        p.setTieneSeguro(RND.nextBoolean());
        p.setCondicionMigratoria(RND.nextInt(5) == 0 ? "Refugiado" : "Regular");
        return p;
    }

    private static Expediente crearExpediente(Persona titular, Parroquia parroquia,
                                               EtapaExpediente etapa, EstadoExpediente estado) {
        Expediente e = new Expediente();
        e.setNumeroFicha(generarNumeroFicha(titular.getNumeroDocumento()));
        e.setFechaInicio(fechaHaceDias(30, 730));
        e.setFechaPrevistaConclusion(fechaEnDias(30, 180));
        e.setEntrevistador(pick(ENTREVISTADORES));
        e.setEtapaActual(etapa);
        e.setEstado(estado);
        e.setParroquia(parroquia);
        e.setTitular(titular);
        e.setColorMarcador(pick(new String[]{"#EF4444", "#F59E0B", "#10B981", "#3B82F6", "#8B5CF6", "#EC4899", null}));
        e.setObservaciones("Expediente generado con datos de prueba. Etapa: " + etapa.name());
        if (estado == EstadoExpediente.CERRADO || estado == EstadoExpediente.SUSPENDIDO) {
            e.setFechaConclusionReal(fechaHaceDias(1, 30));
        }
        return e;
    }

    private static Vivienda crearVivienda(Expediente exp) {
        Vivienda v = new Vivienda();
        v.setExpediente(exp);
        v.setDireccion(exp.getTitular().getDireccion());
        v.setTipo(pick(TipoVivienda.values()));
        v.setTenencia(pick(TenenciaVivienda.values()));
        v.setCondicion(pick(CondicionVivienda.values()));
        return v;
    }

    private static Adendum crearAdendum(Expediente exp) {
        Adendum a = new Adendum();
        a.setExpediente(exp);
        a.setObservaciones("Adendum registrado para " + exp.getNumeroFicha() +
                ". Se detallan los gastos mensuales del núcleo familiar.");
        return a;
    }

    private static GastoMensual crearGasto(Adendum adendum, int idx) {
        CategoriaGasto[] cats = CategoriaGasto.values();
        CategoriaGasto cat = cats[idx % cats.length];
        String[] conceptos = {
            "Recibo ESPH agua", "Recibo ICE electricidad", "Alquiler habitación",
            "Medicamentos crónicos", "Transporte mensual", "Canasta básica",
            "CCSS voluntaria", "Gas doméstico", "Internet básico", "Útiles escolares",
            "Cuota préstamo", "Pago guardería", "Recibo AyA", "Seguro INS",
            "Combustible", "Cuota cooperativa", "Servicio odontológico",
            "Lentes/óptica", "Terapia física", "Cuota de agua comunal"
        };

        GastoMensual g = new GastoMensual();
        g.setAdendum(adendum);
        g.setCategoria(cat);
        g.setConcepto(conceptos[idx % conceptos.length]);
        g.setMonto(monto(8000, 85000));
        g.setFecha(fechaHaceDias(1, 90));
        return g;
    }

    private static MiembroFamiliar crearMiembro(Expediente exp, Persona persona, int idx) {
        MiembroFamiliar m = new MiembroFamiliar();
        m.setExpediente(exp);
        m.setPersona(persona);
        m.setRelacionTitular(pick(RELACIONES_FAMILIAR));
        m.setEsJefatura(idx == 0);
        m.setTrabaja(RND.nextBoolean());
        m.setOcupacion(pick(PROFESIONES));
        m.setIngresoMensual(m.getTrabaja() ? monto(100000, 450000) : BigDecimal.ZERO);
        return m;
    }

    private static DocumentoAdjunto crearDocumento(Expediente exp, Usuario subidoPor, int idx) {
        TipoDocumentoAdjunto[] tipos = TipoDocumentoAdjunto.values();
        TipoDocumentoAdjunto tipo = tipos[idx % tipos.length];
        boolean firmado = tipo == TipoDocumentoAdjunto.CONSENTIMIENTO
                || tipo == TipoDocumentoAdjunto.DICTAMEN
                || (RND.nextInt(4) == 0);

        DocumentoAdjunto d = new DocumentoAdjunto();
        d.setExpediente(exp);
        d.setSubidoPor(subidoPor);
        d.setTipo(tipo);
        d.setDescripcion(tipo.name().replace("_", " ") + " #" + (idx + 1) +
                " - " + exp.getTitular().getApellidos());
        d.setArchivoUrl("/docs/" + exp.getNumeroFicha() + "/" + tipo.name().toLowerCase()
                + "_" + (idx + 1) + ".pdf");
        d.setFechaSubida(fechaHaceDias(1, 200));
        d.setEsDocFirmado(firmado);
        if (firmado) {
            d.setNombreFirmante(exp.getTitular().getNombres() + " " + exp.getTitular().getApellidos());
            d.setFechaFirma(d.getFechaSubida());
        }
        return d;
    }

    private static AsistenciaSolicitada crearAsistencia(Expediente exp, int idx) {
        TipoAsistencia[] tipos = TipoAsistencia.values();
        TipoAsistencia tipo = tipos[idx % tipos.length];
        String[] modalidades = {"Presencial", "Entrega en domicilio", "Voucher", "Transferencia bancaria"};
        String[] frecuencias = {"Mensual", "Quincenal", "Única vez", "Trimestral", "Semestral"};
        String[] duraciones = {"1 mes", "3 meses", "6 meses", "1 año", "Indefinido"};

        AsistenciaSolicitada a = new AsistenciaSolicitada();
        a.setExpediente(exp);
        a.setTipoAsistencia(tipo);
        a.setModalidad(pick(modalidades));
        a.setFrecuencia(pick(frecuencias));
        a.setDuracion(pick(duraciones));
        a.setValor(monto(15000, 120000));
        return a;
    }

    private static Entrevista crearEntrevista(Expediente exp, int idx) {
        String[] obs = {
            "Núcleo familiar en situación de vulnerabilidad alta. Se recomienda ayuda urgente.",
            "Titular con discapacidad, requiere apoyo sostenido a largo plazo.",
            "Familia numerosa con ingresos insuficientes. Niños en edad escolar.",
            "Situación migratoria irregular, en proceso de regularización.",
            "Adulto mayor solo, sin red de apoyo familiar.",
            "Familia con deuda de alquiler atrasada de 3 meses.",
            "Mujer jefa de hogar con hijos menores, sin empleo estable.",
            "Caso de violencia doméstica, requiere seguimiento especial.",
            "Persona con enfermedad crónica sin acceso a medicamentos.",
            "Repatriado reciente, sin recursos económicos básicos."
        };

        Entrevista e = new Entrevista();
        e.setExpediente(exp);
        e.setFecha(fechaHaceDias(1, 300));
        e.setEntrevistador(pick(ENTREVISTADORES));
        e.setObservaciones(obs[idx % obs.length]);
        e.setRecomiendaAyuda(RND.nextInt(5) != 0);
        return e;
    }

    private static ProlongacionAyuda crearProlongacion(Expediente exp, Usuario registradoPor, int idx) {
        String[] motivos = {
            "Situación familiar no ha mejorado. Se extiende ayuda por 3 meses adicionales.",
            "Titular hospitalizado, imposibilidad de generar ingresos.",
            "Pérdida de empleo del cónyuge. Núcleo en crisis.",
            "Caso especial aprobado por Coordinación Vicarial.",
            "Revisión semestral: persiste condición de vulnerabilidad.",
            "Solicitud de la familia, avalada por voluntario de zona."
        };

        ProlongacionAyuda pa = new ProlongacionAyuda();
        pa.setExpediente(exp);
        pa.setRegistradoPor(registradoPor);
        pa.setFechaProlongacion(fechaHaceDias(1, 365));
        pa.setObservaciones(motivos[idx % motivos.length]);
        return pa;
    }

    // ── persistencia ────────────────────────────────────────────────────────

    private static void persist(EntityManager em, Object obj) {
        em.persist(obj);
    }

    /**
     * Guarda un expediente completo con todos sus sub-registros en una sola transacción.
     *
     * @param nMiembros       cantidad de miembros familiares
     * @param nGastos         cantidad de gastos mensuales en el adendum
     * @param nDocs           cantidad de documentos adjuntos
     * @param nAsistencias    cantidad de asistencias solicitadas
     * @param nEntrevistas    cantidad de entrevistas
     * @param nProlongaciones cantidad de prolongaciones de ayuda
     */
    private static void guardarExpedienteCompleto(
            EntityManager em,
            Expediente expediente,
            int baseIdx,
            int nMiembros,
            int nGastos,
            int nDocs,
            int nAsistencias,
            int nEntrevistas,
            int nProlongaciones,
            List<Usuario> usuarios,
            int personaIdx) {

        em.persist(expediente.getTitular());
        em.flush();
        em.persist(expediente);
        em.flush();

        // Vivienda (1:1)
        Vivienda vivienda = crearVivienda(expediente);
        em.persist(vivienda);

        // Miembros familiares (cada uno necesita su propia Persona)
        for (int i = 0; i < nMiembros; i++) {
            Sexo sexo = i % 2 == 0 ? Sexo.FEMENINO : Sexo.MASCULINO;
            Persona miembroPersona = crearPersona(personaIdx * 100 + i + 1000, sexo);
            em.persist(miembroPersona);
            em.flush();
            MiembroFamiliar m = crearMiembro(expediente, miembroPersona, i);
            em.persist(m);
        }

        // Adendum + Gastos
        Adendum adendum = crearAdendum(expediente);
        em.persist(adendum);
        em.flush();
        for (int i = 0; i < nGastos; i++) {
            em.persist(crearGasto(adendum, i));
        }

        // Documentos adjuntos
        Usuario uploader = pick(usuarios);
        for (int i = 0; i < nDocs; i++) {
            em.persist(crearDocumento(expediente, uploader, i));
        }

        // Asistencias solicitadas
        for (int i = 0; i < nAsistencias; i++) {
            em.persist(crearAsistencia(expediente, i));
        }

        // Entrevistas
        for (int i = 0; i < nEntrevistas; i++) {
            em.persist(crearEntrevista(expediente, i));
        }

        // Prolongaciones de ayuda
        if (nProlongaciones > 0) {
            Usuario registrador = pick(usuarios);
            for (int i = 0; i < nProlongaciones; i++) {
                em.persist(crearProlongacion(expediente, registrador, i));
            }
        }
    }

    // ── main ────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== DataSeeder iniciando ===");
        EntityManager em = JPAUtil.getEntityManager();

        try {
            // ── 1. Parroquias ──────────────────────────────────────────────
            em.getTransaction().begin();

            Parroquia p1 = crearParroquia(
                "Parroquia Nuestra Señora de los Ángeles - Hatillo",
                "Vicaria de San José Sur",
                "Hatillo / La Carpio",
                "Hatillo 6, costado sur Escuela República Argentina, San José",
                "2254-1122");

            Parroquia p2 = crearParroquia(
                "Parroquia San Antonio de Padua - Desamparados",
                "Vicaria de San José Este",
                "Desamparados / Alajuelita",
                "Del Parque Central de Desamparados, 200m norte, San José",
                "2259-8833");

            Parroquia p3 = crearParroquia(
                "Parroquia Sagrada Familia - Pavas",
                "Vicaria de San José Oeste",
                "Pavas / Rohrmoser",
                "Barrio Los Laureles, Pavas, San José",
                "2231-4455");

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.flush();
            System.out.println("  + 3 parroquias creadas");

            // ── 2. Usuarios ────────────────────────────────────────────────
            Usuario admin = crearUsuario("Administrador del Sistema",
                    "admin@pastoralsocial.cr", RolUsuario.ADMIN, p1);
            Usuario coord1 = crearUsuario("Hna. María Eugenia Ulate",
                    "mulate@pastoralsocial.cr", RolUsuario.COORDINADOR, p1);
            Usuario coord2 = crearUsuario("Pbro. Carlos Morales",
                    "cmorales@pastoralsocial.cr", RolUsuario.COORDINADOR, p2);
            Usuario vol1 = crearUsuario("Ana Vargas Solís",
                    "avargas@pastoralsocial.cr", RolUsuario.VOLUNTARIO, p1);
            Usuario vol2 = crearUsuario("Roberto Jiménez Chacón",
                    "rjimenez@pastoralsocial.cr", RolUsuario.VOLUNTARIO, p3);
            Usuario consulta = crearUsuario("Lic. Sofía Chacón Rojas",
                    "schaon@vicaria.cr", RolUsuario.CONSULTA_VICARIAL, p1);

            List<Usuario> usuarios = Arrays.asList(admin, coord1, coord2, vol1, vol2, consulta);
            for (Usuario u : usuarios) em.persist(u);
            em.flush();
            System.out.println("  + 6 usuarios creados");

            em.getTransaction().commit();

            // ── 3. Expedientes normales (70) ───────────────────────────────
            List<Parroquia> parroquias = Arrays.asList(p1, p2, p3);
            EtapaExpediente[] etapas = EtapaExpediente.values();
            EstadoExpediente[] estados = EstadoExpediente.values();

            int contadorPersona = 1;

            for (int i = 0; i < 70; i++) {
                em.getTransaction().begin();

                Sexo sexo = i % 3 == 0 ? Sexo.MASCULINO : Sexo.FEMENINO;
                Persona titular = crearPersona(contadorPersona++, sexo);

                EtapaExpediente etapa = etapas[i % etapas.length];
                EstadoExpediente estado;
                if (etapa == EtapaExpediente.APROBADO) {
                    estado = EstadoExpediente.ACTIVO;
                } else if (i % 10 == 9) {
                    estado = EstadoExpediente.CERRADO;
                } else if (i % 12 == 11) {
                    estado = EstadoExpediente.SUSPENDIDO;
                } else {
                    estado = EstadoExpediente.EN_PROCESO;
                }

                Parroquia parroquia = parroquias.get(i % 3);
                Expediente exp = crearExpediente(titular, parroquia, etapa, estado);

                // cantidad de sub-registros varía para simular diversidad
                int nMiembros    = 1 + RND.nextInt(5);
                int nGastos      = 2 + RND.nextInt(8);
                int nDocs        = 1 + RND.nextInt(6);
                int nAsistencias = 1 + RND.nextInt(4);
                int nEntrevistas = 1 + RND.nextInt(3);
                int nProlongs    = i % 4 == 0 ? 1 + RND.nextInt(3) : 0;

                guardarExpedienteCompleto(em, exp, i, nMiembros, nGastos, nDocs,
                        nAsistencias, nEntrevistas, nProlongs, usuarios, contadorPersona);

                em.getTransaction().commit();

                if ((i + 1) % 10 == 0) {
                    System.out.println("  + " + (i + 1) + " expedientes normales guardados...");
                }
            }

            // ── 4. Expedientes "mega" para probar paginación (12) ─────────
            // Cada uno tiene >20 registros por categoría
            System.out.println("  Generando expedientes mega (paginación)...");

            String[] nombresMega = {
                "Familia Rodríguez Vega – caso complejo Hatillo",
                "Familia González Mora – caso complejo Desamparados",
                "Familia Jiménez Solís – adultos mayores Pavas",
                "Familia Vargas Arce – discapacidad múltiple",
                "Familia Chacón Brenes – migración reciente",
                "Familia Calderón Monge – jefatura femenina",
                "Familia Quesada Pérez – enfermedad crónica",
                "Familia Herrera Ulate – desempleo prolongado",
                "Familia Acosta Loría – vivienda precaria",
                "Familia Mora Castro – violencia doméstica",
                "Familia Rojas Vega – caso multifamiliar",
                "Familia Brenes Acosta – caso de largo aliento"
            };

            for (int m = 0; m < 12; m++) {
                em.getTransaction().begin();

                Sexo sexo = m % 2 == 0 ? Sexo.FEMENINO : Sexo.MASCULINO;
                Persona titular = crearPersona(contadorPersona++, sexo);
                // sobrescribir apellidos con nombre descriptivo del caso
                String[] partesMega = nombresMega[m].split("–")[0].replace("Familia ", "").trim().split(" ");
                if (partesMega.length >= 2) {
                    titular.setApellidos(partesMega[0] + " " + partesMega[1]);
                }

                Parroquia parroquia = parroquias.get(m % 3);
                EtapaExpediente etapa = etapas[m % etapas.length];
                Expediente exp = crearExpediente(titular, parroquia, etapa, EstadoExpediente.ACTIVO);
                exp.setObservaciones("CASO COMPLEJO: " + nombresMega[m] +
                        " — expediente con historial extenso para prueba de paginación.");

                guardarExpedienteCompleto(em, exp, 1000 + m,
                        /* miembros      */ 22 + RND.nextInt(5),
                        /* gastos        */ 25 + RND.nextInt(8),
                        /* docs          */ 23 + RND.nextInt(6),
                        /* asistencias   */ 21 + RND.nextInt(5),
                        /* entrevistas   */ 22 + RND.nextInt(4),
                        /* prolongaciones*/ 21 + RND.nextInt(5),
                        usuarios, contadorPersona + m * 50);

                em.getTransaction().commit();
                System.out.println("  + Mega expediente " + (m + 1) + "/12: " + exp.getNumeroFicha());
            }

            System.out.println();
            System.out.println("=== DataSeeder completado ===");
            System.out.println("  Parroquias : 3");
            System.out.println("  Usuarios   : 6");
            System.out.println("  Expedientes: ~82 (70 normales + 12 mega)");
            System.out.println("  Los mega expedientes tienen 21-30 registros por categoría");
            System.out.println("  para probar la paginación (límite 20 por página).");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            System.err.println("ERROR en DataSeeder: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
