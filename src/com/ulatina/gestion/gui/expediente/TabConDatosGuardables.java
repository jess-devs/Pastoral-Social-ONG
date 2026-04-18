package com.ulatina.gestion.gui.expediente;

public interface TabConDatosGuardables {
  /** Lanza IllegalStateException con mensaje legible si hay campos inválidos. */
  void validar() throws IllegalStateException;

  /** Lee los campos de UI y los escribe en los objetos de modelo del contexto. No persiste. */
  void aplicarAlModelo();
}
