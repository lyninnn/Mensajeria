package org.example.mensajeriacliente.util;

import org.example.mensajeriacliente.models.Usuario;

public class UsuarioActual {
    public static Usuario usuarioA;

    public static Usuario getUsuarioA() {
        return usuarioA;
    }

    public static void setUsuarioA(Usuario usuarioA) {
        UsuarioActual.usuarioA = usuarioA;
    }
}
