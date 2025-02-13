package org.example.mensajeriacliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.mensajeriacliente.controllers.AppSettings;
import org.example.mensajeriacliente.controllers.InicioCon;
import org.example.mensajeriacliente.controllers.MensajeView;
import org.example.mensajeriacliente.controllers.UsuarioController;
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

    private UsuarioActual usuarioActual= new UsuarioActual();
    @FXML
    private Button darkModeButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox vBox;

    @FXML
    private Label labelLogin;

    private boolean isDarkMode = AppSettings.isDarkMode();

    private ClienteManager clienteManager =new ClienteManager();


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

                usuarioActual.setUsuarioA( usuario1);

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


    //Método para el DarkMode

    @FXML
    public void toggleDarkMode() {
        if (isDarkMode) {
            // Cambiar a modo claro
            labelLogin.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: 1D1D1F;");
            borderPane.setStyle("-fx-background-color: #F2F2F7; -fx-padding: 40;");
            vBox.setStyle("-fx-background-color: #F2F2F7;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
            AppSettings.setDarkMode(false);
        } else {
            // Cambiar a modo oscuro

            labelLogin.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
            vBox.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
            AppSettings.setDarkMode(true);
        }

        // Alternar el estado del modo
        isDarkMode = !isDarkMode;
    }

    @FXML
    private void initialize() {
        if (AppSettings.isDarkMode() == false){
            labelLogin.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: black;");
            borderPane.setStyle("-fx-background-color: #F2F2F7; -fx-padding: 40;");
            vBox.setStyle("-fx-background-color: #F2F2F7;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
        }else{
            labelLogin.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
            vBox.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
        }
    }
}
