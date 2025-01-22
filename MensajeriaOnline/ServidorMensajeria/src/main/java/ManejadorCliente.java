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
            String insertQuery = "INSERT INTO Usuarios (name,password,telefono) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conexionDB.prepareStatement(insertQuery);
            insertStmt.setString(1, usuario.getNombre());
            insertStmt.setString(2, usuario.getContrasenia());
            insertStmt.setString(3, usuario.getTelefono());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private List<Usuario> listaUsuario(Usuario usuario) {

        try {
            String query = "SELECT * FROM usuarios";
            PreparedStatement stmt = conexionDB.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Agregar cada usuario encontrado a la lista
            while (rs.next()) {
                Usuario usuarioEncontrado = new Usuario();
                usuarioEncontrado.setId(rs.getInt("id")); // Supongamos que tienes un campo ID
                usuarioEncontrado.setNombre(rs.getString("name"));
                usuarioEncontrado.setContrasenia(rs.getString("password"));
                usuarioEncontrado.setLastLogin(LocalDate.parse(rs.getString("last_Online")));
                usuarioEncontrado.setTelefono(rs.getString("telefono"));
                this.usuarioList.add(usuarioEncontrado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.usuarioList;
    }

}
