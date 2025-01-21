package org.example.mensajeriacliente.models;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {
        public static void main(String[] args) {
            String host = "localhost"; // Dirección del servidor
            int puerto = 12345; // Puerto del servidor

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
                            System.out.println(respuesta);
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
    }



