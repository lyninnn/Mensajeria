import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConversationServer {
    private static final int PUERTO = 6000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO + ". Esperando conexiones...");

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Cliente conectado.");
                new Thread(new ClienteHandler(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClienteHandler implements Runnable {
    private Socket cliente;

    public ClienteHandler(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                PrintWriter output = new PrintWriter(cliente.getOutputStream(), true)
        ) {
            String mensaje;
            output.println("Conexión establecida. Escribe un comando (HELP para ver la lista de comandos).");

            while ((mensaje = input.readLine()) != null) {
                mensaje = mensaje.trim().toUpperCase();
                System.out.println("Mensaje recibido: " + mensaje);

                switch (mensaje) {
                    case "HELLO":
                        output.println("Hola, ¿en qué puedo ayudarte?");
                        break;
                    case "TIME":
                        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        output.println("La hora actual es " + hora + ".");
                        break;
                    case "DATE":
                        String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        output.println("La fecha de hoy es " + fecha + ".");
                        break;
                    case "WEATHER":
                        output.println("El clima está soleado y agradable.");
                        break;
                    case "JOKE":
                        output.println("¿Por qué los programadores odian la naturaleza? Porque tiene demasiados bugs.");
                        break;
                    case "QUOTE":
                        output.println("El único límite es tu imaginación.");
                        break;
                    case "HELP":
                        output.println("Comandos disponibles: HELLO, TIME, DATE, WEATHER, JOKE, QUOTE, HELP, STATUS, BYE, EXIT.");
                        break;
                    case "STATUS":
                        output.println("Servidor en línea y funcionando correctamente.");
                        break;
                    case "BYE":
                        output.println("Gracias por la conversación. ¡Que tengas un buen día!");
                        break;
                    case "EXIT":
                        output.println("Adiós. Conexión cerrada.");
                        return;
                    case "PUNTUACION":
                        output.println("Sacaste un 10.LinYi si que eres un genio!");
                        return;
                    default:
                        output.println("No entiendo el comando. Por favor, inténtalo de nuevo.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                cliente.close();
                System.out.println("Cliente desconectado.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
