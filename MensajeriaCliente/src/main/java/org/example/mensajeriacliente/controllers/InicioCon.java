package org.example.mensajeriacliente.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;
import java.util.List;

public class InicioCon {
    FXMLLoader fxmlLoader = null;
    @FXML
    private ListView<Usuario> listUsuarios;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnMostrar;


    @FXML
    private Button darkModeButton;

    @FXML
    private Label contactosLabel;

    @FXML
    private VBox vBoxButtons;

    @FXML
    private VBox vBoxList;

    @FXML
    private AnchorPane anchorPane;

    private boolean isDarkMode = AppSettings.isDarkMode();


    private ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();

    @FXML
    private void initialize() throws IOException, ClassNotFoundException{


        if (AppSettings.isDarkMode() == false){
            anchorPane.setStyle("-fx-background-color: #F2F2F7;");
            contactosLabel.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: black;");
            vBoxList.setStyle("-fx-background-color: #F2F2F7;");
            vBoxButtons.setStyle("-fx-background-color: #F2F2F7;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
        }else{
            anchorPane.setStyle("-fx-background-color: #333333;");
            contactosLabel.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
            vBoxList.setStyle("-fx-background-color: #333333;");
            vBoxButtons.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
        }

        cargarUsuarios();
    }



    private void cargarUsuarios() throws IOException, ClassNotFoundException {
        usuariosList.clear();
        List<Usuario> usuarios = (List<Usuario>) ClienteManager.mostrarListaUsuario();  // Suponiendo que tienes un método en UsuarioDAO para obtener todos los usuarios
        System.out.println(usuarios.size());
        usuariosList.setAll(usuarios);
        listUsuarios.setItems(usuariosList);
    }



    // Método para eliminar un usuario
    @FXML
    public void onEliminar() throws IOException, ClassNotFoundException {
        Usuario usuarioSeleccionado = listUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            boolean exito = ClienteManager.eliminarUsuario(usuarioSeleccionado);
            if (exito) {
                mostrarAlerta("Éxito", "Usuario eliminado correctamente.");

                cargarUsuarios(); // Recargar la lista después de eliminar
            } else {
                mostrarAlerta("Error", "Hubo un problema al eliminar al usuario.");
            }
        } else {
            mostrarAlerta("Error", "Selecciona un usuario para eliminar.");
        }
    }


    // Método para modificar un usuario
    @FXML
    public void onModificar() {
        Usuario usuarioSeleccionado = listUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            try {
                // Inicializar el FXMLLoader y cargar el archivo FXML
                fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/mensajeriacliente/usuarioView.fxml"));
                Parent root = fxmlLoader.load();

                // Obtener el controlador asociado al archivo FXML
                UsuarioController UsuarioCon = fxmlLoader.getController();

                // Pasar los datos del cliente seleccionado al controlador
                UsuarioCon.rellenar(usuarioSeleccionado);


                // Cambiar a la nueva vista (opcional, según tu flujo de trabajo)
                Stage stage = (Stage) listUsuarios.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la vista para actualizar el cliente.");
            }
        } else {
            mostrarAlerta("Error", "Seleccione un cliente para actualizar.");
        }
    }
    @FXML
    public void onMostrarContacto() throws IOException, ClassNotFoundException {
       cargarUsuarios();
    }




    // Método para enviar un mensaje al usuario
    @FXML
    public void onSendMessage() {
        Usuario usuarioSeleccionado = listUsuarios.getSelectionModel().getSelectedItem();
        UsuarioActual.setReceptor(usuarioSeleccionado);
        if (usuarioSeleccionado != null) {
            try {
                // Inicializar el FXMLLoader y cargar el archivo FXML
                fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/mensajeriacliente/mensajeView.fxml"));
                Parent root = fxmlLoader.load();


                // Pasar los datos del cliente seleccionado al controlador


                // Cambiar a la nueva vista (opcional, según tu flujo de trabajo)
                Stage stage = (Stage) listUsuarios.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la vista para actualizar el cliente.");
            }
        } else {
            mostrarAlerta("Error", "Seleccione un cliente para actualizar.");
        }

    }

    // Método para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    @FXML
    public void toggleDarkMode() {
        if (isDarkMode) {
            // Cambiar a modo claro
            anchorPane.setStyle("-fx-background-color: #F2F2F7;");
            contactosLabel.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: 1D1D1F;");
            vBoxList.setStyle("-fx-background-color: #F2F2F7;");
            vBoxButtons.setStyle("-fx-background-color: #F2F2F7;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
            AppSettings.setDarkMode(false);
        } else {
            // Cambiar a modo oscuro
            anchorPane.setStyle("-fx-background-color: #333333;");
            contactosLabel.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
            vBoxList.setStyle("-fx-background-color: #333333;");
            vBoxButtons.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
            AppSettings.setDarkMode(true);
        }

        // Alternar el estado del modo
        isDarkMode = !isDarkMode;
    }


}
