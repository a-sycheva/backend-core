--liquibase formatted sql
--changeset Anastasiya:BCORE-gate-3

CREATE TABLE company_group (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);
