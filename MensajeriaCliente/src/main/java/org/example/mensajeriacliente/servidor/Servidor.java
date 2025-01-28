package org.example.mensajeriacliente.servidor;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {
    private static final int PUERTO = 12345;  // Puerto del servidor
    private static final ExecutorService threadPool = Executors.newCachedThreadPool(); // Para manejar múltiples hilos

    public static void main(String[] args) {
        try (ServerSocket servidorSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                // Esperar una nueva conexión de cliente
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clienteSocket.getInetAddress());

                // Crear un nuevo hilo para manejar la conexión del cliente
                ManejadorCliente manejador = new ManejadorCliente(clienteSocket);
                Thread hiloCliente = new Thread(manejador);
                hiloCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
