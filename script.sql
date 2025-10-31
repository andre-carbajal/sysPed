
CREATE TABLE categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
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
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    action VARCHAR(255) NOT NULL,
    dni VARCHAR(8) NULL,
    name VARCHAR(255) NOT NULL,
    performed_by VARCHAR(255) NULL,
    rol_name VARCHAR(255) NULL,
    when_event DATETIME2(6) NOT NULL
);

CREATE TABLE subcategories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE plates (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
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
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    number INT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('DISPONIBLE', 'ESPERANDO_PEDIDO', 'FALTA_ATENCION', 'FUERA_DE_SERVICIO', 'PEDIDO_ENTREGADO'))
);

CREATE TABLE orders (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    dateandtime_order DATETIME2(6) NOT NULL,
    price_total DECIMAL(38,2) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('CANCELADO', 'EN_PREPARACION', 'LISTO', 'PAGADO', 'PENDIENTE')),
    table_id BIGINT NOT NULL,
    staff_id VARCHAR(8) NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES staffs(dni),
    FOREIGN KEY (table_id) REFERENCES tables(id)
);

CREATE TABLE order_details (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    notes VARCHAR(255) NULL,
    price_unit DECIMAL(38,2) NOT NULL,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    plate_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (plate_id) REFERENCES plates(id)
);

