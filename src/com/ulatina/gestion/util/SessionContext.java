package com.ulatina.gestion.util;

import com.ulatina.gestion.model.Usuario;

/**
 * Contexto de sesión estático — guarda el usuario autenticado durante la sesión.
 */
public final class SessionContext {

  private static Usuario usuarioActual;

  private SessionContext() {}

  public static Usuario getUsuarioActual() {
    return usuarioActual;
  }

  public static void setUsuarioActual(Usuario usuario) {
    usuarioActual = usuario;
  }

  public static void cerrarSesion() {
    usuarioActual = null;
  }
}
