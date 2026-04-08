--liquibase formatted sql
--changeset Anastasiya:BCORE-32-1

CREATE TABLE companies (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    industry VARCHAR(100)
)
