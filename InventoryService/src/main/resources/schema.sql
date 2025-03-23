CREATE TABLE item (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    price FLOAT NOT NULL
);