package org.example.mensajeriacliente.managers;

import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClienteManager {
    private static String host = "localhost"; // Dirección del servidor
    private static int puerto = 12345;        // Puerto del servidor
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static boolean conectado = false;

    // Método estático para iniciar el cliente
    public static boolean login(String nombre, String contraseña) {
        try {
            // Iniciar conexión solo si no está conectada
            if (!conectado) {
                socket = new Socket(host, puerto);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                conectado = true;
                System.out.println("Conectado al servidor en " + host + ":" + puerto);
            }

            // Enviar credenciales al servidor
            Usuario usuario = new Usuario(nombre, contraseña, "");
            out.writeObject("LOGIN");
            out.writeObject(usuario);
            out.flush();

            // Leer respuesta del servidor
            String respuesta = (String) in.readObject();
            if ("OK".equals(respuesta)) {
                System.out.println("Login exitoso.");
                return true;
            } else {
                System.err.println("Login fallido.");
                cerrarConexion(); // Cerrar conexión si el login falla
                return false;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al intentar login: " + e.getMessage());
            cerrarConexion(); // Asegurar que la conexión se cierre en caso de error
        }
        return false;
    }

    public static void cerrarConexion() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            conectado = false;
            System.out.println("Conexión cerrada.");
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // Método para enviar un comando al servidor
    private static synchronized Object enviarComando(String comando, Object data) {
        try {
            if (!conectado) {
                System.err.println("Error: No hay conexión establecida.");
                return null;
            }

            out.writeObject(comando);
            if (data != null) {
                out.writeObject(data);
            }
            out.flush();

            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al enviar comando " + comando + ": " + e.getMessage());
            cerrarConexion(); // Cerrar conexión si hay error
        }
        return null;
    }

    // Método estático para registrar usuario
    public static boolean registra(Usuario usuario) throws IOException, ClassNotFoundException {
        boolean registrado = false;
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Conectado al servidor en " + host + ":" + puerto);
            System.out.println("Registrando usuario: " + usuario);

            // Enviar los datos para el registro al servidor
            out.writeObject("REGISTER");
            out.writeObject(usuario);
            out.flush(); // 🔥 Enviar datos inmediatamente

            // Esperar la respuesta del servidor
            String respuesta = (String) in.readObject();
            registrado = "OK".equals(respuesta);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en el registro: " + e.getMessage());
        }

        System.out.println("Conexión cerrada después del registro.");
        return registrado;
    }

    public static List<Usuario> mostrarListaUsuario() throws IOException, ClassNotFoundException {
        System.out.println("Conectado al servidor en " + host + ":" + puerto);
        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.clear();
        // Enviar el comando LISTA al servidor
        out.writeObject("LISTA");
        out.flush();  // Asegurarse de que los datos se envíen inmediatamente

        // Leer la respuesta del servidor
        Object respuesta = in.readObject();
        System.out.println("Respuesta recibida: " + respuesta);

        // Verificar si la respuesta es del tipo esperado
        if (respuesta instanceof List) {
            listaUsuarios = (List<Usuario>) respuesta;
            System.out.println("Número de usuarios recibidos: " + listaUsuarios.size());

            // Depuración: Imprimir los usuarios recibidos
            for (Usuario usuario : listaUsuarios) {
                System.out.println("Usuario: " + usuario.getNombre());
            }
        } else {
            System.err.println("El servidor no devolvió una lista.");
        }

        return listaUsuarios;
    }
    // Método estático para listar los mensajes
    public static List<Mensaje> listarMensajes(int idUsuario) throws IOException, ClassNotFoundException {

        System.out.println(UsuarioActual.getUsuarioA());
        out.writeObject("OBTENER_MENSAJES");
        out.flush();

        out.writeObject(idUsuario);  // Enviar el id del usuario para listar los mensajes
        out.flush();

        // Leer la respuesta del servidor
        Object respuesta = in.readObject();
        if (respuesta instanceof List) {
            return (List<Mensaje>) respuesta;  // Retornar la lista de mensajes recibidos
        } else {
            System.err.println("Respuesta inesperada al listar mensajes.");
        }

        return new ArrayList<>();  // Retornar lista vacía si hubo error
    }

    public static void marcarMensajeEntregado(int mensajeID) throws IOException, ClassNotFoundException {
        out.writeObject("MARCAR_ENTREGADO");
        out.writeInt(mensajeID);
//        out.writeInt(recep);
        out.flush();

        // Leer respuesta del servidor
        Object respuesta = in.readObject();
        if (!(respuesta instanceof String) || !respuesta.equals("OK")) {
            System.err.println("Error al marcar el mensaje como entregado.");
        }
    }



    // Método estático para eliminar usuario
    public static boolean eliminarUsuario(Usuario usuario) throws IOException, ClassNotFoundException {

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

        return false;
    }

    // Método estático para modificar usuario
    public static boolean modificarUsuario(Usuario usuario) throws IOException, ClassNotFoundException {

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

        return false;
    }

    // Método estático para encontrar usuario
    public static Usuario encontrarUsuario(String nombre) throws IOException, ClassNotFoundException {

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

        return null; // Retorna null si no se encuentra o ocurre un error
    }
    public static boolean enviarMensaje(Mensaje mensaje) throws IOException, ClassNotFoundException {
        out.writeObject("ENVIAR_MENSAJE");
        out.writeObject(mensaje); // Enviar el mensaje al servidor
        out.flush();

        // Leer la respuesta del servidor
        String respuesta = (String) in.readObject();
        return "Mensaje enviado".equals(respuesta);  // Si la respuesta es "OK", el mensaje fue enviado correctamente
    }



    // Método estático para actualizar el estado de un mensaje
    public static boolean actualizarEstadoMensaje(int idMensaje, String estado) throws IOException, ClassNotFoundException {
        out.writeObject("ACTUALIZAR_ESTADO");
        out.writeInt(idMensaje);  // Enviar el id del mensaje
        out.writeObject(estado);  // Enviar el nuevo estado ("pending" o "delivered")
        out.flush();

        // Leer respuesta del servidor
        String respuesta = (String) in.readObject();
        return "OK".equals(respuesta);  // Si la respuesta es "OK", el estado fue actualizado correctamente
    }




}
