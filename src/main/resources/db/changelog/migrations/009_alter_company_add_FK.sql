--liquibase formatted sql
--changeset Anastasiya:BCORE-gate-3-3

ALTER TABLE companies
ADD CONSTRAINT fk_company_group_companies FOREIGN KEY (company_group_id) REFERENCES company_group(id);
