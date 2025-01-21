package org.example.mensajeriacliente.models;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDate;

public class Usuario {
    private int id;
    private String nombre;
    private String telefono;
    private String contrasenia;
    private LocalDate lastLogin;

    // Constructor vacío (opcional)
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(int id,String nombre, String telefono,String contrasenia) {
        this.id=id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.contrasenia = contrasenia;
        lastLogin= LocalDate.now();
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public LocalDate getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDate lastLogin) {
        this.lastLogin = lastLogin;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    // Método para imprimir los detalles del usuario (opcional)
    @Override
    public String toString() {
        return nombre + '\'' +
                telefono + '\'' ;
    }
}



