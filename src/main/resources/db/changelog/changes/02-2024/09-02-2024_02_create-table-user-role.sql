--liquibase formatted sql
--changeset Dmytro Abalmas:09-02-2024_02_create-table-user-role
CREATE TABLE IF NOT EXISTS user_role
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL
);