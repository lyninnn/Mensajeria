drop database mensajeriaonline;
create database mensajeriaonline;
use mensajeriaonline;
CREATE TABLE usuarios (
    IdUser INT(11) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    password VARCHAR(255),
    last_Online DATETIME,
    telefono VARCHAR(20)
);
CREATE TABLE sesiones (
    idSession INT(11) AUTO_INCREMENT PRIMARY KEY,
    userId INT(11),
    modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES usuarios(IdUser)  -- Relacionando con la tabla usuarios
);
CREATE TABLE mensajes (
    idMessage INT(11) AUTO_INCREMENT PRIMARY KEY,
    idTransmitter INT(11),
    idReceiver INT(11),
    msgText TEXT,
    state ENUM('pending', 'delivered') DEFAULT 'pending',
    timeStamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (idTransmitter) REFERENCES usuarios(IdUser),  -- Relaciona con la tabla usuarios (transmisor)
    FOREIGN KEY (idReceiver) REFERENCES usuarios(IdUser)  -- Relaciona con la tabla usuarios (receptor)
);
