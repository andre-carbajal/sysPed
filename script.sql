
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE staffs (
    dni VARCHAR(8) NOT NULL PRIMARY KEY,
    active BIT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol_id BIGINT NOT NULL,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

CREATE TABLE staffs_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    dni VARCHAR(8) NULL,
    name VARCHAR(255) NOT NULL,
    performed_by VARCHAR(255) NULL,
    rol_name VARCHAR(255) NULL,
    when_event DATETIME(6) NOT NULL
);

CREATE TABLE subcategories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE plates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    active BIT NULL,
    description VARCHAR(255) NOT NULL,
    image_base64 TEXT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(38,2) NOT NULL,
    category_id BIGINT NOT NULL,
    subcategory_id BIGINT NULL,
    FOREIGN KEY (subcategory_id) REFERENCES subcategories(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number INT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('DISPONIBLE', 'ESPERANDO_PEDIDO', 'FALTA_ATENCION', 'FUERA_DE_SERVICIO', 'PEDIDO_ENTREGADO'))
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateandtime_order DATETIME(6) NOT NULL,
    price_total DECIMAL(38,2) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('CANCELADO', 'EN_PREPARACION', 'LISTO', 'PAGADO', 'PENDIENTE')),
    table_id BIGINT NOT NULL,
    staff_id VARCHAR(8) NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staffs(dni),
    FOREIGN KEY (table_id) REFERENCES tables(id)
);

CREATE TABLE order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notes VARCHAR(255) NULL,
    price_unit DECIMAL(38,2) NOT NULL,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    plate_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (plate_id) REFERENCES plates(id)
);

CREATE TABLE receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    ruc VARCHAR(11) NULL,
    dni VARCHAR(8) NULL,
    customer_name VARCHAR(120) NULL,
    discount DECIMAL(13,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(13,2) NOT NULL,
    igv DECIMAL(13,2) NOT NULL,
    total DECIMAL(13,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

