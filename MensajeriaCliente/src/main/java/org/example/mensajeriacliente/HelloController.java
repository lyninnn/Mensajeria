package org.example.mensajeriacliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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

    }

    @FXML
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
                clienteManager.iniciarCliente();
                lblStatus.setText("Inicio de sesión exitoso.");
            } else {
                lblStatus.setText("Credenciales incorrectas.");
            }
        }
    }
    @FXML
    private void registrarUsuario() {
        try {
            // Cargar la nueva vista de registro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("usuarioView.fxml"));
            Parent root = loader.load();

            // Obtener la escena actual y reemplazarla con la nueva
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Usuario");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la página de registro.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
