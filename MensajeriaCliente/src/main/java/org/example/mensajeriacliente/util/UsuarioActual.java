package org.example.mensajeriacliente.util;

import org.example.mensajeriacliente.models.Usuario;

public class UsuarioActual {
    private static Usuario usuarioA;
    private static Usuario receptor;

    // Obtener el usuario actual
    public static Usuario getUsuarioA() {
        if (usuarioA == null) {
            throw new IllegalStateException("No hay un usuario autenticado.");
        }
        return usuarioA;
    }

    // Establecer el usuario actual
    public static void setUsuarioA(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        usuarioA = usuario;
    }

    // Limpiar el usuario actual (por ejemplo, al cerrar sesión)
    public static void clearUsuarioA() {
        usuarioA = null;
    }


    public static Usuario getReceptor() {
        if (receptor == null) {
            throw new IllegalStateException("No hay un usuario autenticado.");
        }
        return receptor;
    }

    // Establecer el usuario actual
    public static void setReceptor(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        receptor = usuario;
    }
}