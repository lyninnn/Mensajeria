package org.example.mensajeriacliente.servidor;

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
            // Establecer las entradas y salidas del socket
            out = new ObjectOutputStream(clienteSocket.getOutputStream()); // Cambiado orden
            in = new ObjectInputStream(clienteSocket.getInputStream());


            String comando = (String) in.readObject(); // Leer el primer comando

            if ("LOGIN".equals(comando)) {
                Usuario usuario = (Usuario) in.readObject(); // Leer el objeto Usuario
                if (autenticarUsuario(usuario)) {
                    out.writeObject("OK");
                } else {
                    out.writeObject("ERROR: Credenciales incorrectas");
                }
            } else if ("REGISTER".equals(comando)) {
                try {
                    Usuario usuario = (Usuario) in.readObject();
                    System.out.println("Usuario recibido: " + usuario.getNombre());
                    if (registrarUsuario(usuario)) {
                        out.writeObject("OK");
                    } else {
                        out.writeObject("ERROR: El usuario ya existe");
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Clase no encontrada: " + e.getMessage());
                    e.printStackTrace();
                }


            } else if ("LISTA".equals(comando)) {


                listaUsuario(); // Llenar la lista de usuarios
                System.out.println("Lista de usuarios antes de enviar: " + usuarioList.size());
                if (!usuarioList.isEmpty()) {
                    for (Usuario u : usuarioList) {
                        System.out.println("Enviando usuario: " + u.getNombre());
                    }
                } else {
                    System.out.println("La lista está vacía.");
                }
                out.writeObject(usuarioList); // Enviar la lista al cliente
        } else if ("ELIMINAR".equals(comando)) {
                Usuario usuario = (Usuario) in.readObject();
                eliminarUsuario(usuario);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean autenticarUsuario(Usuario usuario) {
        // Método para autenticar al usuario en la base de datos
        // Aquí debes usar los datos del objeto Usuario
        try {
            String query = "SELECT * FROM usuarios WHERE name = ? AND password = ?";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            System.out.println(usuario);
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getContrasenia()); // Considera usar hashing para la contraseña
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Si el usuario existe, devuelve true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean registrarUsuario(Usuario usuario) {
        // Método para registrar al usuario en la base de datos
        // Verifica si el usuario ya existe y si no, lo inserta
        try {
            // Verificar si el usuario ya existe
            String checkQuery = "SELECT * FROM Usuarios WHERE name = ?";
            PreparedStatement checkStmt = conexionDB.prepareStatement(checkQuery);
            checkStmt.setString(1, usuario.getNombre());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // El usuario ya existe
            }

            // Si el usuario no existe, insertarlo
            String insertQuery = "INSERT INTO Usuarios (name,password,last_Online,telefono) VALUES (?, ?,?, ?)";
            PreparedStatement insertStmt = conexionDB.prepareStatement(insertQuery);
            insertStmt.setString(1, usuario.getNombre());
            insertStmt.setString(2, usuario.getContrasenia());
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
            Usuario usuarioEncontrado = new Usuario();
            System.out.println("estoy aqui2");
            while (rs.next()) {
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




}

