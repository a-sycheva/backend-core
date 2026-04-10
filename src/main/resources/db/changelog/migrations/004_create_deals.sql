--liquibase formatted sql
--changeset Anastasiya:BCORE-32-4

CREATE TABLE deals (
    id UUID PRIMARY KEY NOT NULL,
    lead_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    stage VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_deals_leads FOREIGN KEY (lead_id) REFERENCES leads(id)
    )