package org.example.mensajeriacliente.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.models.Usuario;
import org.example.mensajeriacliente.util.UsuarioActual;

import java.io.IOException;
import java.util.List;

public class MensajeView {

    @FXML
    private ListView<String> listViewMensajes;  // ListView para mostrar los mensajes
    @FXML
    private TextField txtMensaje;  // Campo de texto para escribir el mensaje
    @FXML
    private Button btnEnviar;  // Botón para enviar el mensaje


    @FXML
    private void initialize() {
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
                    // Agregar el mensaje enviado a la ListView
                    listViewMensajes.getItems().add(UsuarioActual.getUsuarioA().getNombre() + ": " + mensajeText);
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
            // Listar los mensajes enviados y recibidos
            List<Mensaje> mensajes = ClienteManager.listarMensajes(UsuarioActual.getReceptor().getId());

            // Mostrar los mensajes en la ListView
            for (Mensaje mensaje : mensajes) {
                // Si el mensaje es del usuario actual o del receptor, mostrarlo en la lista
                if (mensaje.getIdTransmitter() == UsuarioActual.getUsuarioA().getId() ||
                        mensaje.getIdReceiver() == UsuarioActual.getUsuarioA().getId()) {
                    listViewMensajes.getItems().add(mensaje.getIdTransmitter() == UsuarioActual.getUsuarioA().getId()
                            ? UsuarioActual.getUsuarioA().getNombre() + ": " + mensaje.getMsgText()
                            : "Receptor: " + mensaje.getMsgText());
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


}
