-- Create roles records
INSERT INTO roles (id, name, description)
SELECT * FROM (
    VALUES
    (1, 'Admin', 'Admin role'),
    (2, 'User', 'User role')
) AS new_roles (id, name, description)
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE name IN ('Admin', 'User')
);

--  Create user_security records
INSERT INTO users (id, first_name, last_name, position, country, region, city, is_subscribed, description, hard_skill, soft_skill)
SELECT * FROM (
    VALUES
    (8881, 'Dev1', 'Rate1', 'Java Back-End', 'Ukraine', 'Dnipro oblast', 'Dnipro', true, 'Java developer', 0, 0),
    (8882, 'Dev2', 'Rate2', 'ReactJS', 'Ukraine', 'Kyiv oblast', 'Bila Tserkva', true, 'ReactJS developer', 0, 0),
    (8883, 'Dev3', 'Rate3', 'React+JAVA', 'Ukraine', 'Lviv oblast', 'Lviv', true, 'Full Stack developer', 0, 0)
) AS new_users (id, first_name, last_name, position, country, region, city, is_subscribed, description, hardSkill, softSkill)
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = new_users.id
);

--  Create users records
INSERT INTO user_security (id, email, is_verified, created_at, password, role_id, user_id)
SELECT * FROM (
    VALUES
    (7771, 'dev1@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2, 8881),
    (7772, 'dev2@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2, 8882),
    (7773, 'dev3@test.com', true, CURRENT_TIMESTAMP, '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2, 8883)
) AS new_user_security (id, email, is_verified, created_at, password, role_id, user_id)
WHERE NOT EXISTS (
    SELECT 1 FROM user_security WHERE id = new_user_security.id OR email = new_user_security.email
);