-- liquibase formatted sql
-- changeset Anastasiya:gate-4-preparation

-- Создание таблицы invitees
CREATE TABLE IF NOT EXISTS invitees (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Индекс для email (ускоряет поиск по email)
CREATE INDEX IF NOT EXISTS idx_invitees_email ON invitees(email);
