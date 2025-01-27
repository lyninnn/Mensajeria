package org.example.mensajeriacliente.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.mensajeriacliente.managers.ClienteManager;
import org.example.mensajeriacliente.models.Usuario;

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

    private ClienteManager clienteManager=new ClienteManager();
    private ObservableList<Usuario> usuariosList = FXCollections.observableArrayList();

    // Método para cargar todos los usuarios
    public void initialize() {
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = clienteManager.mostrarListaUsuario();  // Suponiendo que tienes un método en UsuarioDAO para obtener todos los usuarios
        usuariosList.setAll(usuarios);
        listUsuarios.setItems(usuariosList);
    }

    // Método para eliminar un usuario
    @FXML
    public void onEliminar() {
        Usuario usuarioSeleccionado = listUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            boolean exito = clienteManager.eliminarUsuario(usuarioSeleccionado);
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
                UsuarioView UsuarioCon = fxmlLoader.getController();

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



    // Método para enviar un mensaje al usuario
    @FXML
    public void onSendMessage() {
        Usuario usuarioSeleccionado = listUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            String mensaje = txtMessage.getText().trim();
            if (!mensaje.isEmpty()) {
                // Aquí puedes agregar la lógica para enviar el mensaje al usuario seleccionado
                // Por ejemplo, almacenar en una base de datos de mensajes
                mostrarAlerta("Mensaje enviado", "Mensaje enviado a " + usuarioSeleccionado.getNombre());
                txtMessage.clear(); // Limpiar el campo de mensaje
            } else {
                mostrarAlerta("Error", "Escribe un mensaje antes de enviar.");
            }
        } else {
            mostrarAlerta("Error", "Selecciona un usuario para enviar el mensaje.");
        }
    }

    // Método para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
