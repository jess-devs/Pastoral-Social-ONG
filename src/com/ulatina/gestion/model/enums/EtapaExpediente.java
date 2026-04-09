package com.ulatina.gestion.model.enums;

public enum EtapaExpediente {
    REGISTRO("Datos"),
    FAMILIA("Familia"),
    VIVIENDA("Vivienda"),
    GASTOS("Adendum"),
    DOCUMENTOS("Docs"),
    CONSENTIMIENTO("Consentimiento"),
    EVALUACION("Entrevistas"),
    APROBADO("Aprobado");

    private final String display;

    EtapaExpediente(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
