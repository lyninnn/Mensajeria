package org.example.mensajeriacliente.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Usuario;

import java.io.IOException;

public class UsuarioController {

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

    @FXML
    private  Button darkModeButton;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label labelRegister;

    private boolean isDarkMode = AppSettings.isDarkMode();



    // Método para registrar al usuario
    @FXML
    public void onRegistrar() throws IOException, ClassNotFoundException {
        String nombreUsuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombreUsuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }
            Usuario usuario = new Usuario(nombreUsuario,confirmPassword,telefono);
        if (btnRegistrar.getText().equals("Registrar")){
            // Crear un nuevo usuario
            // Registrar el usuario en la base de datos
            boolean registroExitoso = clienteManager.registra(usuario);
            // Validar que los campos no estén vacíos

            if (registroExitoso) {
                mostrarAlerta("Éxito", "Usuario registrado exitosamente.");
                // Aquí puedes redirigir a otra vista, como el login
                volverAlMain();

            } else {
                mostrarAlerta("Error", "Hubo un problema al registrar el usuario.");
            }
        }else if (btnRegistrar.getText().equals("Modificar")){
            boolean mofificarExitoso = clienteManager.modificarUsuario(clienteManager.encontrarUsuario(usuario.getNombre()));
            if (mofificarExitoso) {
                mostrarAlerta("Éxito", "Usuario modificado exitosamente.");
                // Aquí puedes redirigir a otra vista, como el login
                volverAlInicio();

            } else {
                mostrarAlerta("Error", "Hubo un problema al modificar el usuario.");
            }
        }
    }

    // Método para mostrar alertas con un título y mensaje
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();

    }
    private void volverAlInicio() {
        try {
            // Cargar la vista principal (main.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mensajeriacliente/inicio.fxml"));
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
    private void volverAlMain() {
        try {
            // Cargar la vista principal (main.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mensajeriacliente/hello-view.fxml"));
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
        public void rellenar(Usuario usuario) {
        txtUsuario.setText(usuario.getNombre());
        txtPassword.setText(usuario.getContrasenia());
        txtConfirmPassword.setText(usuario.getContrasenia());
        txtTelefono.setText(usuario.getTelefono());
        btnRegistrar.setText("Modificar");
        btnRegistrar.setUserData(usuario.getId()); // Guardar el ID del cliente en el botón
    }

    @FXML
    public  void initialize() {
        // Aplicar el estado guardado del darkMode
        if (AppSettings.isDarkMode() == false) {
//            borderPane.setStyle("-fx-background-color: #F2F2F7; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #F2F2F7;");
            labelRegister.setStyle("-fx-text-fill: black;");
            anchorPane.setStyle("-fx-background-color: #E5E5EA;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
        } else {
//            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #333333;");
            labelRegister.setStyle("-fx-text-fill: white;");
            anchorPane.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
        }
    }

    @FXML
    public void toggleDarkMode() {
        if (isDarkMode) {
            // Cambiar a modo claro

//            borderPane.setStyle("-fx-background-color: #F2F2F7; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #F2F2F7;");
            labelRegister.setStyle("-fx-text-fill: black;");
            anchorPane.setStyle("-fx-background-color: #E5E5EA;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
            AppSettings.setDarkMode(false);
        } else {
            // Cambiar a modo oscuro


//            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #333333;");
            labelRegister.setStyle("-fx-text-fill: white;");
            anchorPane.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
            AppSettings.setDarkMode(true);
        }

        // Alternar el estado del modo
        isDarkMode = !isDarkMode;
    }

}
