package com.ulatina.gestion.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Utilidad para el almacenamiento físico de documentos adjuntos.
 * Los archivos se guardan en: docs/<numeroFicha>/<nombre_archivo>
 * BASE_DIR es relativo al directorio de ejecución de la aplicación.
 */
public class FileStorageUtil {

  public static final long MAX_BYTES = 5L * 1024 * 1024; // 5 MB (RNF-12)

  private static final File BASE_DIR = new File(
    System.getProperty("user.dir"),
    "docs"
  );

  /**
   * Obtiene (y crea si no existe) la carpeta del expediente.
   */
  public static File getExpedienteDir(String numeroFicha) {
    File dir = new File(BASE_DIR, sanitizar(numeroFicha));
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }

  /**
   * Copia el archivo origen a la carpeta del expediente.
   * Si ya existe un archivo con el mismo nombre se añade un sufijo numérico.
   * @return El archivo destino copiado.
   */
  public static File copiarArchivo(File origen, String numeroFicha)
    throws IOException {
    File dir = getExpedienteDir(numeroFicha);
    File destino = new File(dir, origen.getName());

    if (destino.exists()) {
      String nombre = origen.getName();
      int punto = nombre.lastIndexOf('.');
      String base = punto > 0 ? nombre.substring(0, punto) : nombre;
      String ext = punto > 0 ? nombre.substring(punto) : "";
      int n = 1;
      do {
        destino = new File(dir, base + "_" + n + ext);
        n++;
      } while (destino.exists());
    }

    Files.copy(
      origen.toPath(),
      destino.toPath(),
      StandardCopyOption.REPLACE_EXISTING
    );
    return destino;
  }

  /**
   * Retorna la ruta relativa que se persiste en archivoUrl:  EXP-xxx/nombre.pdf
   */
  public static String rutaRelativa(String numeroFicha, String nombreArchivo) {
    return sanitizar(numeroFicha) + File.separator + nombreArchivo;
  }

  /**
   * Abre el archivo con la aplicación predeterminada del sistema operativo.
   */
  public static void abrirArchivo(String archivoUrl) throws IOException {
    File f = resolverRuta(archivoUrl);
    if (!f.exists()) {
      throw new IOException("Archivo no encontrado: " + f.getAbsolutePath());
    }
    if (Desktop.isDesktopSupported()) {
      Desktop.getDesktop().open(f);
    } else {
      throw new IOException("Desktop no soportado en este sistema.");
    }
  }

  /**
   * Elimina el archivo físico del disco.
   * @return true si se eliminó o no existía, false si no se pudo eliminar.
   */
  public static boolean eliminarArchivo(String archivoUrl) {
    if (archivoUrl == null || archivoUrl.trim().isEmpty()) return true;
    File f = resolverRuta(archivoUrl);
    return !f.exists() || f.delete();
  }

  // ─── Interno ──────────────────────────────────────────────────────────────

  private static File resolverRuta(String archivoUrl) {
    return new File(BASE_DIR, archivoUrl);
  }

  private static String sanitizar(String nombre) {
    return nombre.replaceAll("[^a-zA-Z0-9._\\-]", "_");
  }
}
