package org.example.mensajeriacliente.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MensajeController {

    @FXML
    private ListView<String> listViewMensajes;   // ListView para mostrar los mensajes
    @FXML
    private TextField txtMensaje;  // Campo de texto para escribir el mensaje
    @FXML
    private Button btnEnviar;  // Botón para enviar el mensaje

    @FXML
    private Label labelUser;

    @FXML
    private  Button darkModeButton;

    @FXML
    private AnchorPane anchorPane;

    private boolean isDarkMode = AppSettings.isDarkMode();



    // Método que maneja el clic en el botón de enviar
    @FXML
    private void onEnviarMensaje() {
        String mensajeText = txtMensaje.getText().trim();  // Obtener el texto del mensaje

        if (!mensajeText.isEmpty()) {
            // Crear un nuevo objeto Mensaje
            Mensaje mensaje = new Mensaje(UsuarioActual.getUsuarioA().getId(), UsuarioActual.getReceptor().getId(), mensajeText);

            // Intentar enviar el mensaje
            try {
                boolean enviado = ClienteManager.enviarMensaje(mensaje);  // Enviar mensaje utilizando ClienteManager
                if (enviado) {
                    cargarMensajes();
                    txtMensaje.clear();  // Limpiar el campo de texto
                } else {
                    mostrarAlerta("Error", "No se pudo enviar el mensaje.");
                }
            } catch (IOException | ClassNotFoundException e) {
                mostrarAlerta("Error", "Ocurrió un error al enviar el mensaje.");
                e.printStackTrace();
            }

        } else {
            // Mostrar alerta si el campo está vacío
            mostrarAlerta("Error", "Escribe un mensaje antes de enviar.");
        }
    }

    // Método para cargar los mensajes del usuario actual con el receptor
    private void cargarMensajes() {

        try {
            listViewMensajes.getItems().clear();
            System.out.println(UsuarioActual.getReceptor().getId());
            // Listar los mensajes enviados y recibidos
            List<Mensaje> mensajes = ClienteManager.listarMensajes(UsuarioActual.getReceptor().getId());

            // Mostrar los mensajes en la ListView
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Formato de fecha y hora

            for (Mensaje mensaje : mensajes) {
                    if (mensaje.getIdReceiver() == UsuarioActual.getUsuarioA().getId()) {
                        ClienteManager.marcarMensajeEntregado(mensaje.getIdMessage());
                    }
                // Si el mensaje es del usuario actual o del receptor, mostrarlo en la lista
                if (mensaje.getIdTransmitter() == UsuarioActual.getUsuarioA().getId() ||
                        mensaje.getIdReceiver() == UsuarioActual.getUsuarioA().getId()) {

                    // Formatear la fecha y hora
                    String fechaHora = mensaje.getTimeStamp().format(formatter);

                    // Construir el mensaje con la fecha
                    String mensajeTexto = (mensaje.getIdTransmitter() == UsuarioActual.getUsuarioA().getId()
                            ? UsuarioActual.getUsuarioA().getNombre() + ": " + mensaje.getMsgText()
                            : UsuarioActual.getReceptor().getNombre() + ": " + mensaje.getMsgText())
                            + " [" + fechaHora + "]"+" ["+mensaje.getState()+"]";

                    // Agregar mensaje a la lista
                    listViewMensajes.getItems().add(mensajeTexto);
                    System.out.println(mensaje.getIdReceiver());
                    System.out.println(UsuarioActual.getUsuarioA().getId());


                }
            }

        } catch (IOException | ClassNotFoundException e) {
            mostrarAlerta("Error", "Ocurrió un error al cargar los mensajes.");
            e.printStackTrace();
        }
    }



    // Método para mostrar alertas en pantalla
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onMouseIn(){
        btnEnviar.setStyle("-fx-background-color: linear-gradient(to bottom, #008CFF, #0066E0); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);");
    }

    @FXML
    private void onMouseOut(){
        btnEnviar.setStyle("-fx-background-color: linear-gradient(to bottom, #007AFF, #005EC4); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);");
    }

    @FXML
    public  void initialize() {

        labelUser.setText(UsuarioActual.getReceptor().getNombre());
        cargarMensajes();
        // Aplicar el estado guardado del darkMode
        if (AppSettings.isDarkMode() == false) {
//            borderPane.setStyle("-fx-background-color: #F2F2F7; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #F2F2F7;");
            labelUser.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: black;");
            anchorPane.setStyle("-fx-background-color: #E5E5EA;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
        } else {
//            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #333333;");
            labelUser.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
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
            labelUser.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: black;");
            anchorPane.setStyle("-fx-background-color: #E5E5EA;");
            darkModeButton.setStyle("-fx-background-color: #1D1D1F; -fx-font-size: 16; -fx-text-fill: #E5E5EA; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Dark Mode 🌙");
            AppSettings.setDarkMode(false);
        } else {
            // Cambiar a modo oscuro


//            borderPane.setStyle("-fx-background-color: #333333; -fx-padding: 40;");
//            vBox.setStyle("-fx-background-color: #333333;");
            labelUser.setStyle("-fx-font-size: 42px; -fx-font-family: 'SF Pro Display', Helvetica, Arial, sans-serif; -fx-text-fill: #1D1D1F; -fx-font-weight: bold; -fx-text-fill: white;");
            anchorPane.setStyle("-fx-background-color: #333333;");
            darkModeButton.setStyle("-fx-background-color: #E5E5EA; -fx-font-size: 16; -fx-text-fill: 1D1D1F; -fx-background-radius: 20; -fx-padding: 5 15; -fx-border-color: transparent; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            darkModeButton.setText("Light Mode ☀");
            AppSettings.setDarkMode(true);
        }

        // Alternar el estado del modo
        isDarkMode = !isDarkMode;
    }


}
