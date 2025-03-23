DROP TABLE IF EXISTS item;
CREATE TABLE item (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
