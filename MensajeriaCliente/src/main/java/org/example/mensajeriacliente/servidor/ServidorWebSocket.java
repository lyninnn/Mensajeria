package org.example.mensajeriacliente.servidor;


import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ServidorWebSocket extends WebSocketServer {
    private static final ConcurrentHashMap<WebSocket, String> clientes = new ConcurrentHashMap<>();

    public ServidorWebSocket(int puerto) {
        super(new InetSocketAddress(puerto));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clientes.put(conn, conn.getRemoteSocketAddress().toString());
        System.out.println("Nuevo cliente WebSocket conectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clientes.remove(conn);
        System.out.println("Cliente WebSocket desconectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Mensaje recibido de WebSocket: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error en WebSocket: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado en el puerto " + getPort());
    }

    // Método para enviar mensajes a todos los clientes conectados
    public void enviarMensajeATodos(String mensaje) {
        for (WebSocket cliente : clientes.keySet()) {
            cliente.send(mensaje);
        }
    }
}

