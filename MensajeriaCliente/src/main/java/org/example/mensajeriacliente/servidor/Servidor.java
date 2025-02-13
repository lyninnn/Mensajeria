package org.example.mensajeriacliente.servidor;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    private static final int PUERTO_SOCKET = 12345;  // Puerto para clientes con Sockets tradicionales
    private static final int PUERTO_WEBSOCKET = 12346; // Puerto para WebSocket
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        // Iniciar el servidor WebSocket en un hilo separado
        threadPool.execute(() -> {
            ServidorWebSocket webSocketServidor = new ServidorWebSocket(PUERTO_WEBSOCKET);
            webSocketServidor.start();
            System.out.println("Servidor WebSocket iniciado en el puerto " + PUERTO_WEBSOCKET);
        });

        // Iniciar el servidor de sockets tradicionales
        try (ServerSocket servidorSocket = new ServerSocket(PUERTO_SOCKET)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO_SOCKET);

            while (true) {
                try {
                    Socket clienteSocket = servidorSocket.accept();
                    System.out.println("Nuevo cliente conectado: " + clienteSocket.getInetAddress());

                    // Usar el threadPool para manejar el cliente
                    threadPool.submit(new ManejadorCliente(clienteSocket));
                } catch (IOException e) {
                    System.err.println("Error al aceptar conexión del cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

}