package com.ulatina.gestion.gui.util;

import java.awt.Color;

/**
 * Constantes de color compartidas por todos los formularios del sistema.
 */
public final class AppColors {

        private AppColors() {
        }

        // --- Base -------------------------------------------------------------------
        public static final Color FONDO = new Color(0xF5F6FA);
        public static final Color PANEL = new Color(0xFFFFFF);
        public static final Color BORDE = new Color(0xD1D5DB);
        public static final Color TEXTO = new Color(0x111827);
        public static final Color TEXTO_GRIS = new Color(0x6B7280);

        // --- Boton primario (verde) -------------------------------------------------
        public static final Color PRIMARIO = new Color(0x22C55E);
        public static final Color PRIMARIO_H = new Color(0x16A34A);

        // --- Boton gris -------------------------------------------------------------
        public static final Color GRIS_BTN = new Color(0xE5E7EB);
        public static final Color GRIS_BTN_H = new Color(0xD1D5DB);

        // --- Tabla -----------------------------------------------------------------
        public static final Color HEADER_TBL = new Color(0xF9FAFB);
        public static final Color FILA_SEL = new Color(0xEFF6FF);

        // --- Azul ------------------------------------------------------------------
        public static final Color AZUL = new Color(0x3B82F6);
        public static final Color AZUL_PANEL = new Color(0xEFF6FF);
        public static final Color AZUL_BORDE = new Color(0x93C5FD);

        // --- Rojo ------------------------------------------------------------------
        public static final Color ROJO = new Color(0xDC2626);
        public static final Color ROJO_H = new Color(0xB91C1C);

        // --- Purpura (FrmDetalleExpediente) ----------------------------------------
        public static final Color PURPURA = new Color(0x6D28D9);
        public static final Color PURPURA_H = new Color(0x5B21B6);

        // --- Ambar -----------------------------------------------------------------
        public static final Color AMBAR_BG = new Color(0xFEF3C7);
        public static final Color AMBAR_FG = new Color(0x92400E);

        // --- Badge ALQUILER --------------------------------------------------------
        public static final Color ALQUILER_BG = new Color(0xFED7AA);
        // foreground usa AMBAR_FG (0x92400E)

        // --- Badge SERVICIOS -------------------------------------------------------
        public static final Color SERVICIOS_BG = new Color(0x99F6E4);
        public static final Color SERVICIOS_FG = new Color(0x065F46);

        // --- Tabla (lineas de grid) ------------------------------------------------
        public static final Color GRID_TBL = new Color(0xF3F4F6);

        // --- Azul profundo (etiquetas informativas) --------------------------------
        public static final Color AZUL_DEEP = new Color(0x1E40AF);

        // --- Rojo claro (boton cerrar sesion) --------------------------------------
        public static final Color ROJO_LIGHT = new Color(0xFCA5A5);

        // --- Dashboard - sidebar ---------------------------------------------------
        public static final Color SIDE_BG = new Color(0x1E2130);
        public static final Color SIDE_ACTV = new Color(0x3B82F6);
        public static final Color SIDE_HOVR = new Color(0x2D3246);
        public static final Color SIDE_SEP = new Color(0x32374B);
        public static final Color SIDE_TXT = new Color(0xB4B9D2);

        // --- Dashboard - tarjetas metricas ----------------------------------------
        public static final Color AZUL_CARD_BG = new Color(0xDBEAFE);
        public static final Color AZUL_CARD_FG = new Color(0x2563EB);
        public static final Color VERDE_BG = new Color(0xDCFCE7);
        public static final Color VERDE_FG = new Color(0x166534);
        public static final Color ROJO_CARD_BG = new Color(0xFEE2E2);
        public static final Color ROJO_CARD_FG = new Color(0x991B1B);
        public static final Color PURP_BG = new Color(0xEDE9FE);

        // --- Badges de EstadoExpediente (indice = ordinal del enum) ----------------
        // 0=ACTIVO, 1=EN_PROCESO, 2=CERRADO, 3=SUSPENDIDO
        public static final Color[] BADGE_BG = {
                new Color(0xDCFCE7),
                new Color(0xFEF9C3),
                new Color(0xFEE2E2),
                new Color(0xF3F4F6)
        };
        public static final Color[] BADGE_FG = {
                new Color(0x166534),
                new Color(0x854D0E),
                new Color(0x991B1B),
                new Color(0x374151)
        };
}