package com.ulatina.gestion.gui.expediente;

/**
 * Contrato que deben implementar las pestañas que participan en el guardado del expediente.
 * FrmDetalleExpediente llama a validar() y luego a aplicarAlModelo() antes de persistir.
 */
public interface TabConDatosGuardables {
  /**
   * Verifica que los campos obligatorios de la pestaña tengan valores válidos.
   *
   * @throws IllegalStateException con un mensaje legible por el usuario si algún campo requerido está vacío o es inválido
   */
  void validar() throws IllegalStateException;

  /**
   * Lee los valores actuales de los campos de la UI y los escribe en los objetos del contexto.
   * No persiste en base de datos; eso lo hace el llamador.
   */
  void aplicarAlModelo();
}
