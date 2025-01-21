package org.example.mensajeriacliente.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;

import java.io.IOException;

public class UsuarioView {

    @FXML
    private TextField txtUsuario; // Campo para el nombre de usuario

    @FXML
    private PasswordField txtPassword; // Campo para la contraseña

    @FXML
    private PasswordField txtConfirmPassword; // Campo para confirmar la contraseña

    @FXML
    private TextField txtTelefono; // Campo para el teléfono


    @FXML
    private Button btnRegistrar; // Botón para registrar el usuario
    private ClienteManager clienteManager;

    // Método para registrar al usuario
    @FXML
    public void onRegistrar() {
        String nombreUsuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validar que los campos no estén vacíos
        if (nombreUsuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }

        // Crear un nuevo usuario

        // Registrar el usuario en la base de datos
        boolean registroExitoso = clienteManager.registra(nombreUsuario,telefono,confirmPassword);

        if (registroExitoso) {
            mostrarAlerta("Éxito", "Usuario registrado exitosamente.");
            // Aquí puedes redirigir a otra vista, como el login
            volverAlMain();

        } else {
            mostrarAlerta("Error", "Hubo un problema al registrar el usuario.");
        }
    }

    // Método para mostrar alertas con un título y mensaje
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
    private void volverAlMain() {
        try {
            // Cargar la vista principal (main.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/whatsapp/main.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();

            // Cambiar la escena a la vista principal
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
