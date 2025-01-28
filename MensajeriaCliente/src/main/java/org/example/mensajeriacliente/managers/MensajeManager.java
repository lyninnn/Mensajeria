package org.example.mensajeriacliente.managers;

import org.example.mensajeriacliente.models.Mensaje;
import org.example.mensajeriacliente.models.Mensaje.EstadoMensaje;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MensajeManager {

    // Lista simulada de mensajes (en un caso real, esto se reemplazaría por una conexión a la base de datos)
    private List<Mensaje> mensajes = new ArrayList<>();

    // Método para enviar un mensaje
    public void enviarMensaje(int idTransmitter, int idReceiver, String msgText) {
        Mensaje mensaje = new Mensaje(
                idTransmitter,
                idReceiver,
                msgText,
                EstadoMensaje.pending, // Estado inicial: pendiente
                LocalDateTime.now()    // Fecha y hora actual
        );
        mensajes.add(mensaje); // Agregar el mensaje a la lista (simulación de guardar en la base de datos)
        System.out.println("Mensaje enviado: " + mensaje);
    }

    // Método para marcar un mensaje como entregado
    public void marcarComoEntregado(int idMessage) {
        for (Mensaje mensaje : mensajes) {
            if (mensaje.getIdMessage() == idMessage) {
                mensaje.setState(EstadoMensaje.delivered); // Cambiar el estado a "entregado"
                System.out.println("Mensaje marcado como entregado: " + mensaje);
                return;
            }
        }
        System.out.println("Mensaje con ID " + idMessage + " no encontrado.");
    }

    // Método para obtener todos los mensajes de un usuario (emisor o receptor)
    public List<Mensaje> obtenerMensajesPorUsuario(int idUsuario) {
        return mensajes.stream()
                .filter(m -> m.getIdTransmitter() == idUsuario || m.getIdReceiver() == idUsuario)
                .collect(Collectors.toList());
    }

    // Método para obtener mensajes pendientes de un usuario
    public List<Mensaje> obtenerMensajesPendientes(int idUsuario) {
        return mensajes.stream()
                .filter(m -> m.getIdReceiver() == idUsuario && m.getState() == EstadoMensaje.pending)
                .collect(Collectors.toList());
    }

    // Método para obtener todos los mensajes (útil para depuración)
    public List<Mensaje> obtenerTodosLosMensajes() {
        return mensajes;
    }

    // Método para eliminar un mensaje por su ID
    public void eliminarMensaje(int idMessage) {
        mensajes.removeIf(m -> m.getIdMessage() == idMessage);
        System.out.println("Mensaje con ID " + idMessage + " eliminado.");
    }
}