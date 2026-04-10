--liquibase formatted sql
--changeset Anastasiya:BCORE-32-3

CREATE TABLE contacts (
    id UUID PRIMARY KEY,
    lead_id UUID,
    first_name VARCHAR(255),
    second_name VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contacts_leads FOREIGN KEY (lead_id) REFERENCES leads(id)
    )