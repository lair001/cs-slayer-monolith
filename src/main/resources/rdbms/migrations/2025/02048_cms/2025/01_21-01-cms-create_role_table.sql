--liquibase formatted sql
--changeset slair:2025_01_21-01-cms-create_role_table

CREATE TABLE role (
    id SMALLINT PRIMARY KEY,
    role VARCHAR(8) NOT NULL UNIQUE
);

INSERT INTO role VALUES (1, 'OWNER');
INSERT INTO role VALUES (2, 'ADMIN');
INSERT INTO role VALUES (3, 'MOD');
INSERT INTO role VALUES (4, 'FREE');
