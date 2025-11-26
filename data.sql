INSERT INTO roles(name) VALUES ('jefe');
INSERT INTO roles(name) VALUES ('administrador');
INSERT INTO roles(name) VALUES ('mozo');
INSERT INTO roles(name) VALUES ('cocinero');
INSERT INTO roles(name) VALUES ('cajero');

INSERT INTO staffs(name, password, dni, rol_id, active) VALUE ('Juan Perez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000000', 1, true);
INSERT INTO staffs(name, password, dni, rol_id, active) VALUE ('Maria Gomez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000001', 2, true);
INSERT INTO staffs(name, password, dni, rol_id, active) VALUE ('Carlos Lopez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000010', 3, true);
INSERT INTO staffs(name, password, dni, rol_id, active) VALUE ('Ana Martinez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000011', 4, true);
INSERT INTO staffs(name, password, dni, rol_id, active) VALUE ('Luis Rodriguez', '$2a$10$4jjG.2p6YihIO6fJvRBIhu/c/MkktCruhtUxiuFzT1.iU78i26xBK', '00000100', 5, true);

INSERT INTO categories (name) VALUES ('Plato de carta');
INSERT INTO categories (name) VALUES ('Menu');
INSERT INTO categories (name) VALUES ('Bebida');
INSERT INTO categories (name) VALUES ('Promoción');

INSERT INTO subcategories (name, category_id) VALUES ('Entrada', 2);
INSERT INTO subcategories (name, category_id) VALUES ('Fondo', 2);
INSERT INTO subcategories (name, category_id) VALUES ('Ceviche', 2);

INSERT INTO subcategories (name, category_id) VALUES ('Cerveza', 3);
INSERT INTO subcategories (name, category_id) VALUES ('Gaseosa', 3);
INSERT INTO subcategories (name, category_id) VALUES ('Natural', 3);

INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Lomo saltado', 'Lomo saltado con papas fritas y arroz', 25.00, 1, NULL, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Ceviche', 'Ceviche de pescado con camote y choclo', 20.00, 2, 3, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Arroz con mariscos', 'Arroz con mariscos frescos y especias', 28.00, 1, NULL, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Pollo a la brasa', 'Pollo a la brasa con papas y ensalada', 32.00, 1, NULL, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Menu Entrada: Ensalada rusa', 'Ensalada rusa como entrada del menú', 10.00, 2, 1, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Menu Fondo: Seco de carne', 'Seco de carne con arroz y frijoles', 18.00, 2, 2, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Cerveza artesanal', 'Cerveza artesanal local', 12.00, 3, 4, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Gaseosa cola', 'Gaseosa cola fría', 6.00, 3, 5, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Jugo natural de naranja', 'Jugo natural recién exprimido', 8.00, 3, 6, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Promo 2x1 Lomo saltado', 'Promoción especial de lomo saltado', 40.00, 4, NULL, true);
INSERT INTO plates (name, description, price, category_id, subcategory_id, active) VALUES ('Promo bebida gratis', 'Promoción de bebida gratis con menú', 0.00, 4, NULL, true);

INSERT INTO tables (number, status) VALUES (1, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (2, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (3, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (4, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (5, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (6, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (7, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (8, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (9, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (10, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (11, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (12, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (13, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (14, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (15, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (16, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (17, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (18, 'DISPONIBLE');
INSERT INTO tables (number, status) VALUES (19, 'DISPONIBLE');

-- Ordenes de diferentes meses y días
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (1, '00000010', '2025-08-15 12:30:00', 'PAGADO', 83.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (3, '00000010', '2025-09-10 19:45:00', 'PAGADO', 126.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (5, '00000010', '2025-10-05 13:15:00', 'PAGADO', 96.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (7, '00000010', '2025-10-20 20:00:00', 'PAGADO', 154.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (2, '00000010', '2025-11-02 14:30:00', 'PAGADO', 76.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (4, '00000010', '2025-11-12 18:20:00', 'PAGADO', 64.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (8, '00000010', '2025-11-18 21:00:00', 'PAGADO', 117.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (6, '00000010', '2025-11-25 12:45:00', 'PAGADO', 145.00);

-- Detalles de orden 1 (Mesa 1 - Agosto 15)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (1, 1, 2, 25.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (1, 7, 2, 12.00, 'Bien fría');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (1, 9, 1, 8.00, NULL);

-- Detalles de orden 2 (Mesa 3 - Septiembre 10)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (2, 4, 2, 32.00, 'Con extra papas');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (2, 2, 1, 20.00, 'Sin picante');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (2, 8, 3, 6.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (2, 7, 2, 12.00, NULL);

-- Detalles de orden 3 (Mesa 5 - Octubre 5)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (3, 3, 2, 28.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (3, 10, 1, 40.00, NULL);

-- Detalles de orden 4 (Mesa 7 - Octubre 20)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (4, 4, 3, 32.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (4, 3, 1, 28.00, 'Extra ají');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (4, 7, 3, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (4, 9, 2, 8.00, NULL);

-- Detalles de orden 5 (Mesa 2 - Noviembre 2)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (5, 5, 2, 10.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (5, 6, 2, 18.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (5, 2, 1, 20.00, NULL);

-- Detalles de orden 6 (Mesa 4 - Noviembre 12)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (6, 1, 2, 25.00, 'Término medio');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (6, 8, 2, 6.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (6, 11, 1, 0.00, 'Aplicar promoción');

-- Detalles de orden 7 (Mesa 8 - Noviembre 18)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (7, 4, 2, 32.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (7, 2, 1, 20.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (7, 7, 2, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (7, 9, 3, 8.00, NULL);

-- Detalles de orden 8 (Mesa 6 - Noviembre 25)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (8, 3, 3, 28.00, 'Bien caliente');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (8, 1, 2, 25.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (8, 7, 1, 12.00, NULL);

-- Recibos para las órdenes (con IGV 18%)
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (1, NULL, '12345678', 'Carlos Mendoza', 0.00, 70.34, 12.66, 83.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (2, '20123456789', NULL, 'Restaurant El Buen Sabor SAC', 0.00, 106.78, 19.22, 126.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (3, NULL, '87654321', 'María Torres', 5.00, 77.12, 13.88, 91.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (4, '20987654321', NULL, 'Eventos y Catering Lima SAC', 10.00, 122.03, 21.97, 144.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (5, NULL, '45678912', 'Pedro Ramírez', 0.00, 64.41, 11.59, 76.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (6, NULL, '78945612', 'Ana Flores', 2.00, 52.54, 9.46, 62.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (7, '20456789123', NULL, 'Corporativo Solutions SAC', 0.00, 99.15, 17.85, 117.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (8, NULL, '32165498', 'Luis Vega', 15.00, 110.17, 19.83, 130.00);

-- ========== 11 NUEVAS ORDENES - OCTUBRE Y NOVIEMBRE ==========

-- Ordenes adicionales octubre-noviembre
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (9, '00000010', '2024-10-08 13:00:00', 'PAGADO', 118.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (10, '00000010', '2024-10-12 19:30:00', 'PAGADO', 88.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (11, '00000010', '2024-10-15 14:45:00', 'PAGADO', 156.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (12, '00000010', '2024-10-22 20:15:00', 'PAGADO', 94.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (13, '00000010', '2024-10-28 12:20:00', 'PAGADO', 72.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (14, '00000010', '2024-11-03 18:50:00', 'PAGADO', 135.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (15, '00000010', '2024-11-07 13:30:00', 'PAGADO', 102.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (16, '00000010', '2024-11-14 21:10:00', 'PAGADO', 168.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (17, '00000010', '2024-11-20 12:00:00', 'PAGADO', 85.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (18, '00000010', '2024-11-23 19:25:00', 'PAGADO', 124.00);
INSERT INTO orders (table_id, staff_id, dateandtime_order, status, price_total) VALUES (19, '00000010', '2024-11-26 15:40:00', 'PAGADO', 110.00);

-- Detalles de orden 9 (Mesa 9 - Octubre 8)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (9, 3, 2, 28.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (9, 1, 1, 25.00, 'Sin cebolla');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (9, 7, 3, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (9, 9, 1, 8.00, 'Con hielo');

-- Detalles de orden 10 (Mesa 10 - Octubre 12)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (10, 2, 2, 20.00, 'Picante aparte');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (10, 5, 2, 10.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (10, 6, 1, 18.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (10, 8, 2, 6.00, NULL);

-- Detalles de orden 11 (Mesa 11 - Octubre 15)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (11, 4, 3, 32.00, 'Extra crujiente');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (11, 1, 1, 25.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (11, 7, 2, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (11, 9, 1, 8.00, NULL);

-- Detalles de orden 12 (Mesa 12 - Octubre 22)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (12, 3, 1, 28.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (12, 2, 2, 20.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (12, 8, 3, 6.00, 'Bien fría');

-- Detalles de orden 13 (Mesa 13 - Octubre 28)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (13, 5, 3, 10.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (13, 6, 2, 18.00, 'Extra frejoles');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (13, 8, 1, 6.00, NULL);

-- Detalles de orden 14 (Mesa 14 - Noviembre 3)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (14, 4, 2, 32.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (14, 3, 1, 28.00, 'Bien picante');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (14, 7, 2, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (14, 9, 3, 8.00, NULL);

-- Detalles de orden 15 (Mesa 15 - Noviembre 7)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (15, 1, 2, 25.00, 'Término medio');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (15, 2, 1, 20.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (15, 7, 2, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (15, 8, 2, 6.00, NULL);

-- Detalles de orden 16 (Mesa 16 - Noviembre 14)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (16, 4, 4, 32.00, 'Para compartir');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (16, 3, 1, 28.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (16, 7, 1, 12.00, NULL);

-- Detalles de orden 17 (Mesa 17 - Noviembre 20)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (17, 2, 2, 20.00, 'Extra camote');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (17, 1, 1, 25.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (17, 8, 3, 6.00, NULL);

-- Detalles de orden 18 (Mesa 18 - Noviembre 23)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (18, 4, 2, 32.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (18, 1, 1, 25.00, 'Bien cocido');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (18, 7, 2, 12.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (18, 9, 1, 8.00, NULL);

-- Detalles de orden 19 (Mesa 19 - Noviembre 26)
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (19, 3, 2, 28.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (19, 2, 1, 20.00, NULL);
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (19, 7, 2, 12.00, 'Sin espuma');
INSERT INTO order_details (order_id, plate_id, quantity, price_unit, notes) VALUES (19, 8, 2, 6.00, NULL);

-- Recibos para las 11 nuevas órdenes (con IGV 18%)
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (9, NULL, '15975348', 'Roberto Sánchez', 0.00, 100.00, 18.00, 118.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (10, NULL, '25874163', 'Carmen Silva', 5.00, 70.34, 12.66, 83.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (11, '20345678912', NULL, 'Distribuidora Norte SAC', 0.00, 132.20, 23.80, 156.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (12, NULL, '36985214', 'Jorge Castillo', 0.00, 79.66, 14.34, 94.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (13, NULL, '47896321', 'Sandra Morales', 3.00, 59.32, 10.68, 70.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (14, '20567891234', NULL, 'Eventos Premium EIRL', 0.00, 114.41, 20.59, 135.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (15, NULL, '58741236', 'Patricia Gutiérrez', 0.00, 86.44, 15.56, 102.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (16, '20678912345', NULL, 'Corporación Gastronómica SAC', 10.00, 134.58, 24.22, 158.80);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (17, NULL, '69852147', 'Miguel Rojas', 0.00, 72.03, 12.97, 85.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (18, NULL, '74185296', 'Isabel Vargas', 0.00, 105.08, 18.92, 124.00);
INSERT INTO receipts (order_id, ruc, dni, customer_name, discount, subtotal, igv, total) VALUES (19, NULL, '85236974', 'Fernando Díaz', 8.00, 86.44, 15.56, 102.00);

