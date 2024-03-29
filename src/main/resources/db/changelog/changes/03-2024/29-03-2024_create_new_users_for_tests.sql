INSERT INTO users (id, email, first_name, last_name, country, is_subscribed, is_verified, created_at, password, role_id)
    VALUES
        (9991, 'dev@test.com','Dev', 'Rate', 'Ukraine', true, true, CURRENT_TIMESTAMP,
        '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
        (9992, 'dev2@test.com','Dev2', 'Rate2', 'Ukraine', true, true, CURRENT_TIMESTAMP,
        '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2),
        (9993, 'dev3@test.com','Dev3', 'Rate3', 'Ukraine', true, true, CURRENT_TIMESTAMP,
        '$2a$12$WPDhaS64wu1a4e3fKbIAaOvs3YC2a31jgV3trdv5hW6mOF.zbhqq2', 2);