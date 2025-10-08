INSERT INTO rol(name)
VALUES ('jefe'),
       ('administrador'),
       ('mozo'),
       ('cocinero');


INSERT INTO staff(name, password, dni, rol_id) VALUE('Juan Perez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000000', 1);
INSERT INTO staff(name, password, dni, rol_id) VALUE('Maria Gomez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000001', 2);
INSERT INTO staff(name, password, dni, rol_id) VALUE('Carlos Lopez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000010', 3);
INSERT INTO staff(name, password, dni, rol_id) VALUE('Ana Martinez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000011', 4);