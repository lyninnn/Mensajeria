package org.example.mensajeriacliente.managers;

import org.example.mensajeriacliente.models.Usuario;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ClienteManager {
    private String host = "localhost"; // Dirección del servidor
    private int puerto = 12345;        // Puerto del servidor
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void iniciarCliente() {
        try {
            socket = new Socket(host, puerto);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            // Inicia el hilo para escuchar mensajes del servidor
            Thread listenerThread = new Thread(() -> escucharRespuestas());
            listenerThread.start();

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        } finally {
            cerrarConexiones();
        }
    }


    private void escucharRespuestas() {
        try {
            Object respuesta;
            while ((respuesta = in.readObject()) != null) {
                System.out.println("Servidor: " + respuesta);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Se perdió la conexión con el servidor.");
        }
    }

    private void cerrarConexiones() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexiones: " + e.getMessage());
        }
    }

    public boolean login(String nombre, String contraseña) {
        try (Socket socket = new Socket(host, puerto);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            // Crear objeto Usuario para enviar al servidor
            Usuario usuario = new Usuario(nombre, contraseña,"");

            // Enviar el objeto al servidor
            out.writeObject("LOGIN");
            out.writeObject(usuario);


            // Esperar la respuesta del servidor
            String respuesta = (String) in.readObject();

            // Supongamos que el servidor responde con "OK" si el login fue exitoso
            return "OK".equals(respuesta);

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }

        return false; // Si no se pudo realizar el login, devolver false
    }

    public boolean registra(Usuario usuario) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Cambiado orden
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {


            System.out.println(usuario);
            // Enviar los datos para el registro al servidor
            out.writeObject("REGISTER");
            out.writeObject(usuario);

            // Esperar la respuesta del servidor
            String respuesta = (String) in.readObject();


            // Supongamos que el servidor responde con "OK" si el registro fue exitoso
            return "OK".equals(respuesta);

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }

        return false; // Si no se pudo realizar el registro, devolver false
    }
    public List<Usuario> mostrarListaUsuario() {
        List<Usuario> listaUsuarios = new ArrayList<>();

        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Enviar el comando LISTA al servidor
            out.writeObject("LISTA");

            // Leer la respuesta del servidor
            Object respuesta = in.readObject();

            // Verificar si la respuesta es del tipo esperado
            if (respuesta instanceof List) {
                listaUsuarios = (List<Usuario>) respuesta;
                System.out.println("Lista de usuarios recibida:");
                for (Usuario usuario : listaUsuarios) {
                    System.out.println("Usuario: " + usuario.getNombre());
                }
            } else {
                System.err.println("El servidor no devolvió una lista.");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al recibir la lista de usuarios: " + e.getMessage());
        }

        return listaUsuarios;
    }

    // Eliminar usuario
    public boolean eliminarUsuario(Usuario usuario) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Enviar solicitud para eliminar usuario
            out.writeObject("ELIMINAR");
            out.writeObject(usuario);

            // Leer respuesta del servidor
            Object respuesta = in.readObject();
            if (respuesta instanceof Boolean) {
                return (Boolean) respuesta;
            } else {
                System.err.println("Respuesta inesperada al eliminar usuario.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }

        return false;
    }

    // Modificar usuario
    public boolean modificarUsuario(Usuario usuario) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Enviar solicitud para modificar usuario
            out.writeObject("MODIFICAR");
            out.writeObject(usuario); // Enviar el objeto usuario con los cambios

            // Leer respuesta del servidor
            Object respuesta = in.readObject();
            if (respuesta instanceof Boolean) {
                return (Boolean) respuesta;
            } else {
                System.err.println("Respuesta inesperada al modificar usuario.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
        }

        return false;
    }
    public Usuario encontrarUsuario(String nombre) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Enviar solicitud para buscar usuario
            out.writeObject("BUSCAR"); // Corregido de "BUSACR" a "BUSCAR"
            out.writeObject(nombre); // Enviar el nombre del usuario a buscar

            // Leer respuesta del servidor
            Object respuesta = in.readObject();
            if (respuesta instanceof Usuario) {
                return (Usuario) respuesta; // Retorna el usuario encontrado
            } else {
                System.err.println("Respuesta inesperada al buscar usuario.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }

        return null; // Retorna null si no se encuentra o ocurre un error
    }



}
