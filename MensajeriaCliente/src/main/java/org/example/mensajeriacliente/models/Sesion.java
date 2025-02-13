package org.example.mensajeriacliente.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Sesion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idSession;
    private int userId;
    private Timestamp modified;

    public Sesion(int idSession, int userId, Timestamp modified) {
        this.idSession = idSession;
        this.userId = userId;
        this.modified = modified;
    }

    public Sesion() {
    }

    public Sesion(int userId, Timestamp modified) {
        this.userId = userId;
        this.modified = modified;
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "Sesion{" +
                ", userId=" + userId +
                ", modified=" + modified +
                '}';
    }
}

