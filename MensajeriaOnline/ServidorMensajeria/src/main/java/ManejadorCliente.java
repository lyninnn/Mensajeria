import java.io.*;
import java.net.*;
import java.sql.*;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Connection conexionDB;
    private String nombreUsuario;

    public ManejadorCliente(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
        try {
            // Configuración de la conexión a la base de datos
            this.conexionDB = DriverManager.getConnection("jdbc:mysql://localhost:3306/MensajeriaOnline", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Establecer las entradas y salidas del socket
            in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            out = new PrintWriter(clienteSocket.getOutputStream(), true);

            // 1. Autenticación del usuario
            out.println("Introduzca su nombre de usuario:");
            nombreUsuario = in.readLine();
            out.println("Introduzca su contraseña:");
            String contrasena = in.readLine();

            if (autenticarUsuario(nombreUsuario, contrasena)) {
                out.println("¡Autenticado correctamente!");

                // 2. Cambiar el estado de la sesión a "online" cuando el usuario se conecta
                actualizarEstadoConexion(true);

                // 3. Enviar mensajes pendientes al usuario
                enviarMensajesPendientes();

                // 4. Manejo de mensajes
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    if (mensaje.equalsIgnoreCase("salir")) {
                        break;
                    }
                    // Guardar el mensaje en la base de datos
                    guardarMensaje(nombreUsuario, mensaje);
                    out.println("Mensaje enviado: " + mensaje);
                }

            } else {
                out.println("Credenciales incorrectas." +
                        "registrate");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 5. Al desconectarse, actualizar el estado de la sesión a "offline"
                actualizarEstadoConexion(false);

                if (conexionDB != null) {
                    conexionDB.close();
                }
                if (clienteSocket != null) {
                    clienteSocket.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Método para autenticar al usuario
    private boolean autenticarUsuario(String usuario, String contrasena) {
        try {
            String query = "SELECT * FROM Usuarios WHERE name = ? AND password = ?";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena); // Aquí debería realizarse una comprobación segura (hash de la contraseña)
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para guardar un mensaje en la base de datos
    private void guardarMensaje(String emisor, String mensaje) {
        try {
            String query = "INSERT INTO Mensajes (idTransmitter, msgText, state) VALUES ((SELECT IdUser FROM Usuarios WHERE name = ?), ?, 'pending')";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, emisor);
            stmt.setString(2, mensaje);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para actualizar el estado de conexión del usuario (online u offline)
    private void actualizarEstadoConexion(boolean online) {
        try {
            String query = "UPDATE Sesiones SET online = ?, modified = CURRENT_TIMESTAMP WHERE userId = (SELECT IdUser FROM Usuarios WHERE name = ?)";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setBoolean(1, online);
            stmt.setString(2, nombreUsuario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar mensajes pendientes al usuario
    private void enviarMensajesPendientes() {
        try {
            String query = "SELECT msgText FROM Mensajes WHERE idReceiver = (SELECT IdUser FROM Usuarios WHERE name = ?) AND state = 'pending'";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, nombreUsuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mensajePendiente = rs.getString("msgText");
                out.println("Mensaje pendiente: " + mensajePendiente);

                // Cambiar el estado de este mensaje a 'delivered'
                String updateQuery = "UPDATE Mensajes SET state = 'delivered' WHERE msgText = ? AND idReceiver = (SELECT IdUser FROM Usuarios WHERE name = ?)";
                PreparedStatement updateStmt = conexionDB.prepareStatement(updateQuery);
                updateStmt.setString(1, mensajePendiente);
                updateStmt.setString(2, nombreUsuario);
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
