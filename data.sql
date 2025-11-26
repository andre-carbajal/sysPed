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
