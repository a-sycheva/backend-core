--liquibase formatted sql
--changeset Anastasiya:BCORE-gate-3-2

ALTER TABLE companies
ADD COLUMN IF NOT EXISTS company_group_id UUID
