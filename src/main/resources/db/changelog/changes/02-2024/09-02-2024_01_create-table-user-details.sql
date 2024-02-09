--liquibase formatted sql
--changeset Dmytro Abalmas:09-02-2024_01_create-table-user-details
CREATE TABLE IF NOT EXISTS user_details
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(100)                        NOT NULL,
    first_name    VARCHAR(100)                        NOT NULL,
    last_name     VARCHAR(100)                        NOT NULL,
    country       VARCHAR(100)                        NOT NULL,
    is_subscribed BOOLEAN                             NOT NULL,
    is_verified   BOOLEAN                             NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
