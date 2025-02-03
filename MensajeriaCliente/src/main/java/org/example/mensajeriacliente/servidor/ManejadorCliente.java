package org.example.mensajeriacliente.servidor;
import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.util.UsuarioActual;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.example.mensajeriacliente.models.Usuario;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Connection conexionDB;
    private List<Usuario> usuarioList = new ArrayList<>();
    private List<Mensaje> mensajeList = new ArrayList<>();

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
        boolean continuar = true; // Bandera para controlar el bucle

        try {
            out = new ObjectOutputStream(clienteSocket.getOutputStream());
            in = new ObjectInputStream(clienteSocket.getInputStream());

            while (continuar) {
                String comando = (String) in.readObject();

                switch (comando) {
                    case "LOGIN":
                        Usuario usuarioLogin = (Usuario) in.readObject();
                        boolean autenticado = autenticarUsuario(usuarioLogin);
                        out.writeObject(autenticado ? "OK" : "ERROR: Credenciales incorrectas");
                        out.flush();

                        if (autenticado) {
                            UsuarioActual.setUsuarioA(buscarUsuarioPorNombre(usuarioLogin.getNombre()));
                        }
                        break;

                    case "REGISTER":
                        Usuario usuarioRegistro = (Usuario) in.readObject();
                        out.writeObject(registrarUsuario(usuarioRegistro) ? "OK" : "ERROR: El usuario ya existe");
                        break;

                    case "LISTA":
                        listaUsuario();
                        out.writeObject(usuarioList);
                        System.out.println("Número de usuarios en el servidor: " + usuarioList.size());
                        break;

                    case "ELIMINAR":
                        Usuario usuarioEliminar = (Usuario) in.readObject();
                        out.writeObject(eliminarUsuario(usuarioEliminar));
                        break;

                    case "MODIFICAR":
                        System.out.println("recibido");
                        Usuario usuarioModificar = (Usuario) in.readObject();
                        out.writeObject(modificarUsuario(usuarioModificar));
                        break;

                    case "BUSCAR":
                        String nombre = (String) in.readObject();  // Recibir el nombre del usuario a buscar
                        Usuario usuario = buscarUsuarioPorNombre(nombre);  // Llamar al método para buscar el usuario

                        // Enviar el usuario encontrado o null si no se encontró
                        out.writeObject(usuario);
                        break;

                    case "SALIR":
                        continuar = false; // Finaliza el bucle y cierra la conexión
                        break;
                    case "ENVIAR_MENSAJE":
                        Mensaje mensaje = (Mensaje) in.readObject();

                        boolean mensajeEnviado = enviarMensaje(mensaje.getIdTransmitter(), mensaje.getIdReceiver(),mensaje.getMsgText());
                        out.writeObject(mensajeEnviado ? "Mensaje enviado" : "Error al enviar el mensaje");
                        break;
                    case "MARCAR_ENTREGADO":
                        try {
                            int idMensaje = in.readInt();  // Recibir ID del mensaje

                            String query = "UPDATE mensajes SET state = 'delivered' WHERE idMessage = ?";

                            PreparedStatement stmt = conexionDB.prepareStatement(query);
                            stmt.setInt(1, idMensaje);
//                            stmt.setInt(2, idRecep);
                            int filasActualizadas = stmt.executeUpdate();

                            // Responder al cliente
                            out.writeObject(filasActualizadas > 0 ? "OK" : "ERROR");
                        } catch (SQLException e) {
                            out.writeObject("ERROR: " + e.getMessage());
                        }
                        break;


                    case "OBTENER_MENSAJES":
                        try {
                            // Verificar que el usuario esté autenticado
                            Usuario usuarioActual = UsuarioActual.getUsuarioA();
                            if (usuarioActual == null) {
                                out.writeObject("ERROR: No hay un usuario autenticado.");
                                break;
                            }

                            // Recibir el ID del receptor
                            int idReceptor = (int) in.readObject();
                            System.out.println(idReceptor);
                            // Obtener los mensajes entre el emisor y el receptor
                            List<Mensaje> mensajes = obtenerMensajes(usuarioActual.getId(), idReceptor);
                            System.out.println("Mensajes obtenidos: " + mensajes);

                            // Enviar la lista de mensajes al cliente
                            out.writeObject(mensajes);
                        } catch (ClassNotFoundException e) {
                            out.writeObject("ERROR: Tipo de dato inesperado.");
                        } catch (IOException e) {
                            System.err.println("Error al enviar/recibir datos: " + e.getMessage());
                        }
                        break;

                    default:
                        out.writeObject("ERROR: Comando no reconocido");
                }
            }
        } catch (EOFException e) {
            System.err.println("Cliente desconectado: " + clienteSocket.getInetAddress());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                clienteSocket.close(); // Cerrar el socket al salir del bucle
                System.out.println("Conexión cerrada con el cliente: " + clienteSocket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Usuario buscarUsuarioPorNombre(String nombre) {
        try {
            // Consulta SQL para buscar el usuario por nombre
            String query = "SELECT * FROM Usuarios WHERE name = ?";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();

            // Si se encuentra un usuario, crear el objeto Usuario
            if (rs.next()) {
                Usuario usuarioEncontrado = new Usuario();
                usuarioEncontrado.setId(rs.getInt("IdUser"));
                usuarioEncontrado.setNombre(rs.getString("name"));
                usuarioEncontrado.setContrasenia(rs.getString("password"));
                usuarioEncontrado.setLastLogin(rs.getDate("last_Online").toLocalDate());
                usuarioEncontrado.setTelefono(rs.getString("telefono"));
                return usuarioEncontrado;
            } else {
                return null;  // Si no se encuentra el usuario, retornar null
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;  // Retornar null en caso de error
        }
    }




    private boolean autenticarUsuario(Usuario usuario) {
        try {
            String query = "SELECT password FROM Usuarios WHERE name = ?";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, usuario.getNombre());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                // Comparar la contraseña ingresada con la almacenada
                return BCrypt.checkpw(usuario.getContrasenia(), hashedPassword);
            }
            return false; // El usuario no existe
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private boolean registrarUsuario(Usuario usuario) {
        try {
            // Verificar si el usuario ya existe
            String checkQuery = "SELECT * FROM Usuarios WHERE name = ?";
            PreparedStatement checkStmt = conexionDB.prepareStatement(checkQuery);
            checkStmt.setString(1, usuario.getNombre());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // El usuario ya existe
            }

            // Cifrar la contraseña antes de almacenarla
            String hashedPassword = BCrypt.hashpw(usuario.getContrasenia(), BCrypt.gensalt());

            // Insertar el usuario con la contraseña cifrada
            String insertQuery = "INSERT INTO Usuarios (name, password, last_Online, telefono) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conexionDB.prepareStatement(insertQuery);
            insertStmt.setString(1, usuario.getNombre());
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, String.valueOf(LocalDate.now()));
            insertStmt.setString(4, usuario.getTelefono());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Usuario> listaUsuario() {
        try {
            System.out.println("Ejecutando consulta para obtener usuarios...");
            String query = "SELECT * FROM usuarios";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            System.out.println("estoy aqui");
            ResultSet rs = stmt.executeQuery();

            usuarioList.clear(); // Asegúrate de limpiar la lista antes de llenarla
            System.out.println("estoy aqui2");
            while (rs.next()) {
                Usuario usuarioEncontrado = new Usuario();
                usuarioEncontrado.setId(rs.getInt("IdUser"));
                usuarioEncontrado.setNombre(rs.getString("name"));
                usuarioEncontrado.setContrasenia(rs.getString("password"));
                usuarioEncontrado.setLastLogin(rs.getDate("last_Online").toLocalDate());
                usuarioEncontrado.setTelefono(rs.getString("telefono"));
                usuarioList.add(usuarioEncontrado);
                System.out.println("Usuario encontrado: " + usuarioEncontrado.getNombre());
            }

            System.out.println("Número de usuarios encontrados: " + usuarioList.size());

        } catch (SQLException e) {
            System.err.println("Error al ejecutar la consulta: " + e.getMessage());
        }

        return usuarioList;
    }
    private boolean eliminarUsuario(Usuario usuario) {
        // Método para eliminar al usuario en la base de datos
        try {
            // Verificar si el usuario existe
            String checkQuery = "SELECT * FROM Usuarios WHERE name = ?";
            PreparedStatement checkStmt = conexionDB.prepareStatement(checkQuery);
            checkStmt.setString(1, usuario.getNombre());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // El usuario existe, ahora procederemos a eliminarlo
                String deleteQuery = "DELETE FROM Usuarios WHERE IdUser = ?";
                PreparedStatement deleteStmt = conexionDB.prepareStatement(deleteQuery);
                deleteStmt.setInt(1, usuario.getId());
                int affectedRows = deleteStmt.executeUpdate();

                return affectedRows > 0;  // Si se eliminó al menos un usuario, el método retorna true
            } else {
                return false; // El usuario no existe
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error en la eliminación
        }
    }
    private boolean modificarUsuario(Usuario usuario) {
        try {
            String checkQuery = "SELECT password FROM Usuarios WHERE IdUser = ?";
            try (PreparedStatement checkStmt = conexionDB.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, usuario.getId());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password");

                    String newHashedPassword = usuario.getContrasenia().equals(storedHashedPassword) ?
                            storedHashedPassword : BCrypt.hashpw(usuario.getContrasenia(), BCrypt.gensalt());

                    String updateQuery = "UPDATE Usuarios SET name = ?, password = ?, telefono = ? WHERE IdUser = ?";
                    try (PreparedStatement updateStmt = conexionDB.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, usuario.getNombre());
                        updateStmt.setString(2, newHashedPassword);
                        updateStmt.setString(3, usuario.getTelefono());
                        updateStmt.setInt(4, usuario.getId());

                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean enviarMensaje(int idTransmitter, int idReceiver, String msgText) {
        try {
            // Inserta el mensaje en la base de datos con estado 'pending'
            String query = "INSERT INTO mensajes (idTransmitter, idReceiver, msgText, state, timeStamp) VALUES (?, ?, ?, 'pending', ?)";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setInt(1, idTransmitter);
            stmt.setInt(2, idReceiver);
            stmt.setString(3, msgText);
            stmt.setTimestamp(4, Timestamp.valueOf( LocalDateTime.now()));

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Mensaje> obtenerMensajes(int idTransmitter, int idReceiver) {
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            // Consulta los mensajes entre el emisor y el receptor
            String query = "SELECT idMessage,idTransmitter, idReceiver, msgText, state, timeStamp FROM mensajes " +
                    "WHERE (idTransmitter = ? AND idReceiver = ?) OR (idTransmitter = ? AND idReceiver = ?) " +
                    "ORDER BY timeStamp";
            PreparedStatement stmt = conexionDB.prepareStatement(query);

            // Establecer los parámetros de la consulta

            stmt.setInt(1, idTransmitter);
            stmt.setInt(2, idReceiver);
            stmt.setInt(3, idReceiver);
            stmt.setInt(4, idTransmitter);

            // Ejecutar la consulta
            ResultSet rs = stmt.executeQuery();

            // Procesar los resultados y crear objetos Mensaje
            while (rs.next()) {
                Mensaje mensaje = new Mensaje();
                mensaje.setIdMessage(rs.getInt("idMessage"));
                mensaje.setIdTransmitter(rs.getInt("idTransmitter")); // Establecer el ID del emisor
                mensaje.setIdReceiver(rs.getInt("idReceiver"));       // Establecer el ID del receptor
                mensaje.setMsgText(rs.getString("msgText"));            // Establecer el texto del mensaje
                mensaje.setState(Mensaje.EstadoMensaje.valueOf(rs.getString("state")));             // Establecer el estado del mensaje
                mensaje.setTimeStamp(rs.getTimestamp("timeStamp").toLocalDateTime());       // Establecer la fecha del mensaje
                mensajes.add(mensaje); // Añadir el mensaje a la lista
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mensajes; // Devolver la lista de mensajes
    }

    private boolean actualizarEstadoMensaje(int idMessage, String estado) {
        try {
            // Actualiza el estado del mensaje (por ejemplo, de 'pending' a 'delivered')
            String query = "UPDATE mensajes SET state = ? WHERE idMessage = ?";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            stmt.setString(1, estado);
            stmt.setInt(2, idMessage);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }






}

