INSERT INTO users (id, email, first_name, last_name, country, is_subscribed, is_verified, created_at, password, role_id)
SELECT * FROM (
    VALUES
    (7771, 'dev1@test.com', 'Dev1', 'Rate1', 'Ukraine', true, true, CURRENT_TIMESTAMP,
    '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
    (7772, 'dev2@test.com', 'Dev2', 'Rate2', 'Ukraine', true, true, CURRENT_TIMESTAMP,
    '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
    (7773, 'dev3@test.com', 'Dev3', 'Rate3', 'Ukraine', true, true, CURRENT_TIMESTAMP,
    '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2)
) AS new_users (id, email, first_name, last_name, country, is_subscribed, is_verified, created_at, password, role_id)
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE id = new_users.id OR email = new_users.email
);
