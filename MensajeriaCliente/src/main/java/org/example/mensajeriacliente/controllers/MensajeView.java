package org.example.mensajeriacliente.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MensajeView {

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

    private UsuarioActual usuarioActual=new UsuarioActual();

    private boolean isDarkMode = AppSettings.isDarkMode();


    @FXML
    private void initialize() {
       cargarMensajes();
        iniciarEscuchaMensajes();
    }

    private void iniciarEscuchaMensajes() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Mensaje> mensajes = ClienteManager.listarMensajes();
                Platform.runLater(() -> actualizarMensajesEnVista(mensajes));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);
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


    // Método que maneja el clic en el botón de enviar
    @FXML
    private void onEnviarMensaje() {
        String mensajeText = txtMensaje.getText().trim();  // Obtener el texto del mensaje

        if (!mensajeText.isEmpty()) {
            // Crear un nuevo objeto Mensaje
            Mensaje mensaje = new Mensaje(0,0,mensajeText);

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
    private void escucharMensajes() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                while (true) {
                    // Obtener los mensajes del receptor
                    List<Mensaje> mensajes = ClienteManager.listarMensajes();

                    // Actualizar la interfaz solo si hay nuevos mensajes
                    Platform.runLater(() -> actualizarMensajesEnVista(mensajes));

                    // Esperar un poco antes de hacer la siguiente consulta
                    Thread.sleep(1000);  // Espera de 1 segundo, puedes ajustarlo según tus necesidades
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        hiloEscucha.setDaemon(true);  // Para que el hilo se cierre cuando se cierre la aplicación
        hiloEscucha.start();
    }

    private void actualizarMensajesEnVista(List<Mensaje> mensajes) {
        listViewMensajes.getItems().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Mensaje mensaje : mensajes) {
//            if (mensaje.getIdTransmitter() == usuarioActual.getUsuarioA().getId() ||
//                    mensaje.getIdReceiver() == usuarioActual.getUsuarioA().getId()) {

                // Formatear la fecha y hora
                String fechaHora = mensaje.getTimeStamp().format(formatter);

                // Construir el mensaje con la fecha
                String mensajeTexto = ( mensaje.getMsgText())
                        + " [" + fechaHora + "]"+" ["+mensaje.getState()+"]";

                // Agregar mensaje a la lista
                listViewMensajes.getItems().add(mensajeTexto);
            }
        }



    private void cargarMensajes() {
        try {

            List<Mensaje> mensajes = ClienteManager.listarMensajes();

            if (mensajes == null || mensajes.isEmpty()) {
                return;
            }

            Platform.runLater(() -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                for (Mensaje mensaje : mensajes) {
//                    if (mensaje.getIdReceiver() ==usuarioActual.getUsuarioA().getId()) {
//                        try {
//                            ClienteManager.marcarMensajeEntregado(mensaje.getIdMessage());
//                        } catch (IOException | ClassNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }


                    String fechaHora = mensaje.getTimeStamp().format(formatter);
                    String mensajeTexto = (mensaje.getMsgText())
                            + " [" + fechaHora + "]" + " [" + mensaje.getState() + "]";


                    if (!listViewMensajes.getItems().contains(mensajeTexto)) {
                        listViewMensajes.getItems().add(mensajeTexto);
                    }
                }
            });

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
    @FXML
    private void onMouseIn(){
        btnEnviar.setStyle("-fx-background-color: linear-gradient(to bottom, #008CFF, #0066E0); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);");
    }

    @FXML
    private void onMouseOut(){
        btnEnviar.setStyle("-fx-background-color: linear-gradient(to bottom, #007AFF, #005EC4); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 5, 0, 0, 2);");
    }


    public void rellenarA(Usuario usuarioA) {
        this.usuarioActual.setUsuarioA(usuarioA);
    }

}

