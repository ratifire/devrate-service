--liquibase formatted sql
--changeset Dmytro Abalmas:09-02-2024_03_create-table-user-security
CREATE TABLE IF NOT EXISTS user_security
(
    id           BIGSERIAL PRIMARY KEY,
    password     VARCHAR(245)  NOT NULL,
    user_id      BIGINT UNIQUE NOT NULL,
    user_role_id BIGINT        NOT NULL

);
