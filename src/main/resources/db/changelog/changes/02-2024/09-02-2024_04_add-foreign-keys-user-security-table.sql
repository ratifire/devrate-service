ALTER TABLE user_security
    ADD CONSTRAINT fk_user_role FOREIGN KEY (user_role_id) REFERENCES user_role(id),
    ADD CONSTRAINT fk_user_details FOREIGN KEY (user_id) REFERENCES user_details(id);
