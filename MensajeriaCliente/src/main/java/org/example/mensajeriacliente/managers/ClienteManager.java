package org.example.mensajeriacliente.managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteManager {
    private String host = "localhost"; // Dirección del servidor
    private int puerto = 12345;        // Puerto del servidor

    public void iniciarCliente() {
        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Conectado al servidor en " + host + ":" + puerto);

            // Hilo para leer mensajes del servidor de manera asíncrona
            Thread listener = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = in.readLine()) != null) {
                        System.out.println("Servidor: " + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("Se perdió la conexión con el servidor.");
                }
            });
            listener.start();

            // Enviar datos al servidor
            String entradaUsuario;
            while ((entradaUsuario = stdIn.readLine()) != null) {
                out.println(entradaUsuario);
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

    public boolean login(String usuario, String password) {
        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Enviar las credenciales al servidor (puedes estructurarlas como desees)
            out.println("LOGIN " + usuario + " " + password);

            // Esperar la respuesta del servidor
            String respuesta = in.readLine();

            // Supongamos que el servidor responde con "OK" si el login fue exitoso
            return "OK".equals(respuesta);

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }

        return false; // Si no se pudo realizar el login, devolver false
    }
    public boolean registra(String usuario, String telefono, String password) {
        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Enviar los datos para el registro al servidor (puedes estructurarlo como desees)
            out.println("REGISTER " + usuario + " " + telefono + " " + password);

            // Esperar la respuesta del servidor
            String respuesta = in.readLine();

            // Supongamos que el servidor responde con "OK" si el registro fue exitoso
            return "OK".equals(respuesta);

        } catch (UnknownHostException e) {
            System.err.println("No se pudo conectar al host: " + host);
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }

        return false; // Si no se pudo realizar el registro, devolver false
    }


}

