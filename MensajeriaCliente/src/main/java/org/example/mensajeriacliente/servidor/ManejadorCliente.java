package org.example.mensajeriacliente.servidor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.example.mensajeriacliente.models.Usuario;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Connection conexionDB;
    private List<Usuario> usuarioList = new ArrayList<>();

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
            out = new ObjectOutputStream(clienteSocket.getOutputStream());
            in = new ObjectInputStream(clienteSocket.getInputStream());

            while (true) {
                String comando = (String) in.readObject();

                switch (comando) {
                    case "LOGIN":
                        Usuario usuarioLogin = (Usuario) in.readObject();
                        out.writeObject(autenticarUsuario(usuarioLogin) ? "OK" : "ERROR: Credenciales incorrectas");
                        break;
                    case "REGISTER":
                        Usuario usuarioRegistro = (Usuario) in.readObject();
                        out.writeObject(registrarUsuario(usuarioRegistro) ? "OK" : "ERROR: El usuario ya existe");
                        break;
                    case "LISTA":
                        listaUsuario();
                        out.writeObject(usuarioList);
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
                        return; // Finaliza el hilo
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
                clienteSocket.close();
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
        // Método para modificar datos de un usuario en la base de datos
        try {
            // Verificar si el usuario existe
            String checkQuery = "SELECT * FROM Usuarios WHERE name = ?";
            PreparedStatement checkStmt = conexionDB.prepareStatement(checkQuery);
            checkStmt.setString(1, usuario.getNombre());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // El usuario existe, ahora procederemos a modificarlo

                // Cifrar la contraseña si ha sido modificada
                String hashedPassword = BCrypt.hashpw(usuario.getContrasenia(), BCrypt.gensalt());

                String updateQuery = "UPDATE Usuarios SET name = ?, password = ?, telefono = ? WHERE IdUser = ?";
                PreparedStatement updateStmt = conexionDB.prepareStatement(updateQuery);
                updateStmt.setString(1, usuario.getNombre());
                updateStmt.setString(2, hashedPassword);  // Usamos la contraseña cifrada
                updateStmt.setString(3, usuario.getTelefono());  // Asumí que el teléfono es un String, sin necesidad de convertir a int
                updateStmt.setInt(4, usuario.getId());

                int affectedRows = updateStmt.executeUpdate();

                // Cerrar recursos
                updateStmt.close();
                rs.close();
                checkStmt.close();

                return affectedRows > 0; // Retorna true si se actualizó al menos un usuario
            } else {
                rs.close();
                checkStmt.close();
                return false; // El usuario no existe
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error en la modificación
        }
    }





}

