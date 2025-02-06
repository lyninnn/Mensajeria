package org.example.mensajeriacliente.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MensajeView {

    @FXML
    private ListView<String> listViewMensajes;   // ListView para mostrar los mensajes
    @FXML
    private TextField txtMensaje;  // Campo de texto para escribir el mensaje
    @FXML
    private Button btnEnviar;  // Botón para enviar el mensaje

    @FXML
    private Button btnVolver;
    @FXML
    private Label labelUser;


    @FXML
    private void initialize() {
        labelUser.setText(UsuarioActual.getReceptor().getNombre());
        cargarMensajes();  // Cargar los mensajes al iniciar la vista
    }


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
    private void volverMain(){
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/mensajeriacliente/inicio.fxml"));
            Parent root = fxmlLoader.load();

            // Obtener la ventana actual
            Stage stage = (Stage) btnVolver.getScene().getWindow(); // btnEnviar es un botón en la interfaz

            // Configurar la nueva escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista para actualizar el cliente.");
        }
    }


}
