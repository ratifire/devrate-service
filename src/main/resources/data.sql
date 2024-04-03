--  Create user_security records
INSERT INTO user_security (id, email, is_verified, created_at, password, role_id)
SELECT * FROM (
    VALUES
    (7771, 'dev1@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
    (7772, 'dev2@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
    (7773, 'dev3@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2)
) AS new_user_security (id, email, is_verified, created_at, password, role_id)
WHERE NOT EXISTS (
    SELECT 1 FROM user_security WHERE id = new_user_security.id OR email = new_user_security.email
);

--  Create users records
INSERT INTO users (id, first_name, last_name, position, country, region, city, is_subscribed, description, user_id)
SELECT * FROM (
    VALUES
    (8881, 'Dev1', 'Rate1', 'Java Back-End', 'Ukraine', 'Dnipro oblast', 'Dnipro', true, 'Java developer', 7771),
    (8882, 'Dev2', 'Rate2', 'ReactJS', 'Ukraine', 'Kyiv oblast', 'Bila Tserkva', true, 'ReactJS developer', 7772),
    (8883, 'Dev3', 'Rate3', 'React+JAVA', 'Ukraine', 'Lviv oblast', 'Lviv', true, 'Full Stack developer', 7773)
) AS new_users (id, first_name, last_name, position, country, region, city, is_subscribed, description, user_id)
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = new_users.id OR user_id = new_users.user_id
);