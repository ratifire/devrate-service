--liquibase formatted sql
--changeset Dmytro Abalmas:09-02-2024_04_add-foreign-keys-user-security-table
ALTER TABLE user_security
    ADD CONSTRAINT fk_user_role FOREIGN KEY (user_role_id) REFERENCES user_role(id);
ALTER TABLE user_security
    ADD CONSTRAINT fk_user_details FOREIGN KEY (user_id) REFERENCES user_details(id);
