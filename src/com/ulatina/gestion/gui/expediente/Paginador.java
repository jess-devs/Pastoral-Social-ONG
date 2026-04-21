package com.ulatina.gestion.gui.expediente;

import java.util.Collections;
import java.util.List;

/**
 * Paginador genérico en memoria de 20 elementos por página.
 * Divide una lista completa en páginas y expone métodos de navegación.
 * No hace ninguna consulta a base de datos; trabaja sobre la lista cargada con cargar().
 *
 * @param <T> tipo de elemento de la lista
 */
public class Paginador<T> {

  /** Número fijo de elementos por página. */
  private static final int TAMANO = 20;

  /** Lista completa de elementos cargada en la última llamada a cargar(). */
  private List<T> datos = Collections.emptyList();

  /** Índice de la página actual, basado en cero. */
  private int pagina = 0;

  /**
   * Reemplaza el dataset y reinicia la página a cero.
   *
   * @param todos lista completa de elementos; si es null se trata como vacía
   */
  public void cargar(List<T> todos) {
    datos = todos != null ? todos : Collections.emptyList();
    pagina = 0;
  }

  /**
   * Devuelve los elementos de la página actual.
   *
   * @return sublista de hasta TAMANO elementos, o lista vacía si la página está fuera de rango
   */
  public List<T> getPagina() {
    int desde = pagina * TAMANO;
    int hasta = Math.min(desde + TAMANO, datos.size());
    return desde < datos.size()
      ? datos.subList(desde, hasta)
      : Collections.emptyList();
  }

  /**
   * Devuelve la lista completa sin paginar.
   *
   * @return vista no modificable del dataset completo
   */
  public List<T> getTodos() {
    return Collections.unmodifiableList(datos);
  }

  /**
   * Indica si existe al menos una página anterior a la actual.
   *
   * @return true si pagina mayor que cero
   */
  public boolean hayAnterior() {
    return pagina > 0;
  }

  /**
   * Indica si existe al menos una página posterior a la actual.
   *
   * @return true si hay más elementos más allá de la página actual
   */
  public boolean haySiguiente() {
    return (pagina + 1) * TAMANO < datos.size();
  }

  /**
   * Retrocede a la página anterior si la hay; no hace nada si ya está en la primera.
   */
  public void anterior() {
    if (hayAnterior()) pagina--;
  }

  /**
   * Avanza a la página siguiente si la hay; no hace nada si ya está en la última.
   */
  public void siguiente() {
    if (haySiguiente()) pagina++;
  }

  /**
   * Indica si el dataset tiene más elementos que el tamaño de una página.
   *
   * @return true si el total de elementos supera TAMANO
   */
  public boolean necesitaPaginacion() {
    return datos.size() > TAMANO;
  }

  /**
   * Genera una etiqueta de navegación con el número de página actual y el total de páginas.
   *
   * @return cadena con formato "Página N de M"
   */
  public String etiqueta() {
    int total = Math.max(1, (int) Math.ceil(datos.size() / (double) TAMANO));
    return "Página " + (pagina + 1) + " de " + total;
  }
}
