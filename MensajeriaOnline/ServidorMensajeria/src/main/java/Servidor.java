import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Servidor {
    private static final int PUERTO = 12345;  // Puerto del servidor
    private static final ExecutorService threadPool = Executors.newCachedThreadPool(); // Para manejar múltiples hilos

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getInetAddress());
                threadPool.submit(new ManejadorCliente(clienteSocket));  // Maneja cada cliente en un hilo
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
