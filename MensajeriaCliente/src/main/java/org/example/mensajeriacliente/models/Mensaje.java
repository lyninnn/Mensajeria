package org.example.mensajeriacliente.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idMessage;          // Identificador único del mensaje
    private int idTransmitter;      // Identificador del emisor del mensaje
    private int idReceiver;         // Identificador del receptor del mensaje
    private String msgText;         // Texto del mensaje
    private EstadoMensaje state;    // Estado del mensaje (pending o delivered)
    private LocalDateTime timeStamp; // Fecha y hora del mensaje

    // Enumeración para el estado del mensaje
    public enum EstadoMensaje {
        pending, delivered
    }

    // Constructor vacío
    public Mensaje() {
    }


    // Constructor con parámetros
    public Mensaje(int idTransmitter, int idReceiver, String msgText, EstadoMensaje state, LocalDateTime timeStamp) {
        this.idTransmitter = idTransmitter;
        this.idReceiver = idReceiver;
        this.msgText = msgText;
        this.state = state;
        this.timeStamp = timeStamp;
    }

    public Mensaje(int idMessage, int idTransmitter, int idReceiver, String msgText, EstadoMensaje state, LocalDateTime timeStamp) {
        this.idMessage = idMessage;
        this.idTransmitter = idTransmitter;
        this.idReceiver = idReceiver;
        this.msgText = msgText;
        this.state = state;
        this.timeStamp = timeStamp;
    }

    // Getters y Setters
    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getIdTransmitter() {
        return idTransmitter;
    }

    public void setIdTransmitter(int idTransmitter) {
        this.idTransmitter = idTransmitter;
    }

    public int getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(int idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public EstadoMensaje getState() {
        return state;
    }

    public void setState(EstadoMensaje state) {
        this.state = state;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    // Método toString para imprimir los detalles del mensaje
    @Override
    public String toString() {
        return "Mensaje{" +
                "idMessage=" + idMessage +
                ", idTransmitter=" + idTransmitter +
                ", idReceiver=" + idReceiver +
                ", msgText='" + msgText + '\'' +
                ", state=" + state +
                ", timeStamp=" + timeStamp +
                '}';
    }
}