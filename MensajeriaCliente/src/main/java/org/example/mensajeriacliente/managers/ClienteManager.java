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
    public void iniciarCliente() {
        try (Socket socket = new Socket(host, puerto);
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            // Hilo para leer mensajes del servidor de manera asíncrona
            Thread listener = new Thread(() -> {
                try {
                    Object respuesta;
                    while ((respuesta = in.readObject()) != null) {
                        System.out.println("Servidor: " + respuesta);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Se perdió la conexión con el servidor.");
                }
            });
            listener.start();

            // Enviar datos al servidor
            String entradaUsuario;
            while ((entradaUsuario = stdIn.readLine()) != null) {
                out.writeObject(entradaUsuario);
                if (entradaUsuario.equalsIgnoreCase("salir")) {
                    System.out.println("Desconectando...");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
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

            // Enviar la solicitud al servidor
            out.writeObject("LISTA");

            // Leer la respuesta del servidor
            Object respuesta = in.readObject();

            if (respuesta instanceof List<?>) {
                // Verificar que la respuesta sea una lista y procesarla
                listaUsuarios = (List<Usuario>) respuesta;
                System.out.println("Lista de usuarios recibida:");
                for (Usuario usuario : listaUsuarios) {
                    System.out.println("Nombre: " + usuario.getNombre() + ", Teléfono: " + usuario.getTelefono());
                }
            } else {
                System.err.println("Respuesta inesperada del servidor: " + respuesta);
            }

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error al procesar la respuesta del servidor: " + e.getMessage());
        }

        return listaUsuarios; // Retornar la lista de usuarios, aunque esté vacía en caso de error
    }
    // Eliminar usuario
    public boolean eliminarUsuario(int usuarioId) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Enviar solicitud para eliminar usuario
            out.writeObject("ELIMINAR");
            out.writeInt(usuarioId);

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


}
