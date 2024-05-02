--  Create user_security records
INSERT INTO users (id, first_name, last_name, position, country, region, city, is_subscribed, description)
SELECT * FROM (
    VALUES
    (8881, 'Dev1', 'Rate1', 'Java Back-End', 'Ukraine', 'Dnipro oblast', 'Dnipro', true, 'Java developer'),
    (8882, 'Dev2', 'Rate2', 'ReactJS', 'Ukraine', 'Kyiv oblast', 'Bila Tserkva', true, 'ReactJS developer'),
    (8883, 'Dev3', 'Rate3', 'React+JAVA', 'Ukraine', 'Lviv oblast', 'Lviv', true, 'Full Stack developer')
) AS new_users (id, first_name, last_name, position, country, region, city, is_subscribed, description)
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

--  Create specialisations
INSERT INTO specialisations (id, specialisation)
SELECT * FROM (
    VALUES
    (1, 'SOFTWARE_ENGINEERING'),
    (2, 'DATA_SCIENCE'),
    (3, 'CYBER_SECURITY'),
    (4, 'NETWORK_ENGINEERING'),
    (5, 'WEB_DEVELOPMENT'),
    (6, 'MOBILE_APP_DEVELOPMENT'),
    (7, 'DATABASE_ADMINISTRATION'),
    (8, 'CLOUD_COMPUTING'),
    (9, 'MACHINE_LEARNING'),
    (10, 'ARTIFICIAL_INTELLIGENCE'),
    (11, 'GAME_DEVELOPMENT'),
    (12, 'DEVOPS'),
    (13, 'UI_UX_DESIGN'),
    (14, 'IT_PROJECT_MANAGEMENT'),
    (15, 'BLOCKCHAIN_TECHNOLOGY'),
    (16, 'COMPUTER_GRAPHICS'),
    (17, 'IT_SUPPORT'),
    (18, 'COMPUTER_HARDWARE_ENGINEERING'),
    (19, 'INTERNET_OF_THINGS'),
    (20, 'BIG_DATA_ANALYTICS'),
    (21, 'COMPUTER_VISION'),
    (22, 'ROBOTICS'),
    (23, 'QA_TESTING'),
    (24, 'TECHNICAL_WRITING'),
    (25, 'IT_TRAINING_AND_EDUCATION'),
    (26, 'COMPUTER_FORENSICS'),
    (27, 'IT_POLICY_AND_GOVERNANCE'),
    (28, 'EMBEDDED_SYSTEMS'),
    (29, 'AUGMENTED_VIRTUAL_REALITY'),
    (30, 'IT_CONSULTING'),
    (31, 'QUANTUM_COMPUTING'),
    (32, 'JAVA'),
    (33, 'PYTHON'),
    (34, 'JAVASCRIPT'),
    (35, 'C_SHARP'),
    (36, 'C_PLUS_PLUS'),
    (37, 'RUBY'),
    (38, 'SWIFT'),
    (39, 'KOTLIN'),
    (40, 'GO'),
    (41, 'PHP'),
    (42, 'HTML_CSS'),
    (43, 'SQL'),
    (44, 'R'),
    (45, 'MATLAB'),
    (46, 'PERL'),
    (47, 'TYPESCRIPT'),
    (48, 'SCALA'),
    (49, 'OBJECTIVE_C'),
    (50, 'LUA'),
    (51, 'HASKELL'),
    (52, 'JULIA'),
    (53, 'COBOL'),
    (54, 'FORTRAN'),
    (55, 'ADA'),
    (56, 'DART'),
    (57, 'COFFEESCRIPT'),
    (58, 'SHELL_SCRIPTING')
) AS new_specialisations (id, specialisation)
WHERE NOT EXISTS (
    SELECT 1 FROM specialisations WHERE id = new_specialisations.id
);