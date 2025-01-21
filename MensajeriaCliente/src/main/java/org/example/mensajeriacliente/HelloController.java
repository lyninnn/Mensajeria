package org.example.mensajeriacliente;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.mensajeriacliente.managers.ClienteManager;

public class HelloController {

    @FXML
    private TextField txtUsuario; // Campo para el nombre de usuario

    @FXML
    private PasswordField txtPassword; // Campo para la contraseña

    @FXML
    private Button btnLogin; // Botón para iniciar sesión

    @FXML
    private Button btnRegistrar; // Botón para registrar usuario

    @FXML
    private Label lblStatus; // Etiqueta para mostrar mensajes de estado

    private ClienteManager clienteManager;

    public HelloController() {
        this.clienteManager = new ClienteManager(); // Instancia del gestor del cliente
    }

    @FXML
    private void initialize() {
        // Configurar los manejadores de eventos para los botones
        btnLogin.setOnAction(event -> iniciarSesion());
        btnRegistrar.setOnAction(event -> registrarUsuario());
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Aquí puedes integrar lógica de clienteManager para manejar el inicio de sesión
        if (clienteManager != null) {
            // Simulación de un inicio de sesión exitoso
            boolean loginExitoso = clienteManager.login(usuario, password); // Asume que existe un método `login`

            if (loginExitoso) {
                lblStatus.setText("Inicio de sesión exitoso.");
            } else {
                lblStatus.setText("Credenciales incorrectas.");
            }
        }
    }

    private void registrarUsuario() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Lógica para registrar un nuevo usuario
        if (clienteManager != null) {
            boolean registroExitoso = clienteManager.registrar(usuario, password); // Asume que existe un método `registrar`

            if (registroExitoso) {
                lblStatus.setText("Usuario registrado exitosamente.");
            } else {
                lblStatus.setText("El usuario ya existe o hubo un error.");
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
