CREATE TABLE usuario (
    id VARCHAR(36) NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created DATETIME,
    modified DATETIME,
    last_login DATETIME,
    token VARCHAR(36),
    is_active BOOLEAN,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY (email)
);

CREATE TABLE telefono (
    id INT AUTO_INCREMENT,
    numero VARCHAR(20),
    tipo VARCHAR(20),
    user_id VARCHAR(36),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES usuario (id)
);
