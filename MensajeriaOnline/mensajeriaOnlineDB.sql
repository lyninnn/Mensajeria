-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS MensajeriaOnline;

-- Usar la base de datos
USE MensajeriaOnline;

-- Tabla Usuarios
CREATE TABLE Usuarios (
    IdUser INT AUTO_INCREMENT PRIMARY KEY,  -- Identificador único del usuario
    name VARCHAR(100) NOT NULL,             -- Nombre del usuario
    password VARCHAR(255) NOT NULL,         -- Contraseña encriptada del usuario
    last_Online DATETIME,                   -- Última vez que el usuario estuvo conectado
    UNIQUE (name)                           -- Asegura que los nombres de usuario sean únicos
);

-- Tabla Mensajes
CREATE TABLE Mensajes (
    idMessage INT AUTO_INCREMENT PRIMARY KEY,  -- Identificador único del mensaje
    idTransmitter INT,                         -- Identificador del emisor
    idReceiver INT,                            -- Identificador del receptor
    msgText TEXT NOT NULL,                     -- Texto del mensaje
    state ENUM('pending', 'delivered') DEFAULT 'pending',  -- Estado del mensaje
    timeStamp DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Fecha y hora del mensaje
    FOREIGN KEY (idTransmitter) REFERENCES Usuarios(IdUser),  -- Relación con la tabla de usuarios
    FOREIGN KEY (idReceiver) REFERENCES Usuarios(IdUser)     -- Relación con la tabla de usuarios
);

-- Tabla Sesiones
CREATE TABLE Sesiones (
    idSession INT AUTO_INCREMENT PRIMARY KEY,  -- Identificador único de la sesión
    userId INT,                               -- Identificador del usuario
    online BOOLEAN DEFAULT FALSE,              -- Estado de conexión (TRUE = conectado, FALSE = desconectado)
    modified DATETIME DEFAULT CURRENT_TIMESTAMP,  -- Fecha de la última actualización de la sesión
    FOREIGN KEY (userId) REFERENCES Usuarios(IdUser)  -- Relación con la tabla de usuarios
);
