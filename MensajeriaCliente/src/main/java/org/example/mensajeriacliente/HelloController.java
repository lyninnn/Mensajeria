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
import org.example.mensajeriacliente.controllers.InicioCon;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;

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



    @FXML
    private void initialize() {

    }

    @FXML
    private void iniciarSesion() throws IOException, ClassNotFoundException {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "Por favor, completa todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        // Aquí puedes integrar lógica de clienteManager para manejar el inicio de sesión

            // Simulación de un inicio de sesión exitoso
            System.out.println(usuario+" "+password);

            if (ClienteManager.login(usuario, password)){
                Usuario usuario1 = ClienteManager.encontrarUsuario(usuario);
                UsuarioActual.setUsuarioA( usuario1);
                redirigirAInicio();
            };

    }
    private void redirigirAInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("inicio.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de InicioCon y pasar la instancia de ClienteManager
            InicioCon inicioCon = loader.getController();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la página de inicio.", Alert.AlertType.ERROR);
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
