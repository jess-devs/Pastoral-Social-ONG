package com.ulatina.gestion.gui.expediente;

import java.util.Collections;
import java.util.List;

public class Paginador<T> {

  private static final int TAMANO = 20;
  private List<T> datos = Collections.emptyList();
  private int pagina = 0;

  public void cargar(List<T> todos) {
    datos = todos != null ? todos : Collections.emptyList();
    pagina = 0;
  }

  public List<T> getPagina() {
    int desde = pagina * TAMANO;
    int hasta = Math.min(desde + TAMANO, datos.size());
    return desde < datos.size()
      ? datos.subList(desde, hasta)
      : Collections.emptyList();
  }

  public List<T> getTodos() {
    return Collections.unmodifiableList(datos);
  }

  public boolean hayAnterior() {
    return pagina > 0;
  }

  public boolean haySiguiente() {
    return (pagina + 1) * TAMANO < datos.size();
  }

  public void anterior() {
    if (hayAnterior()) pagina--;
  }

  public void siguiente() {
    if (haySiguiente()) pagina++;
  }

  public boolean necesitaPaginacion() {
    return datos.size() > TAMANO;
  }

  public String etiqueta() {
    int total = Math.max(1, (int) Math.ceil(datos.size() / (double) TAMANO));
    return "Página " + (pagina + 1) + " de " + total;
  }
}
