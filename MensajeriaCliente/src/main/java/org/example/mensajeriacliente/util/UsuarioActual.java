package org.example.mensajeriacliente.util;

import org.example.mensajeriacliente.models.Usuario;

public class UsuarioActual {
    private Usuario usuarioActual;
    private Usuario receptor;

    public UsuarioActual(Usuario usuarioActual, Usuario receptor) {
        this.usuarioActual = usuarioActual;
        this.receptor = receptor;
    }

    public UsuarioActual() {
    }

    public Usuario getUsuarioA() {
        return usuarioActual;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setUsuarioA(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }
}

