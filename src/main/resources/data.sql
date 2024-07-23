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
INSERT INTO users (id, first_name, last_name, status, country, city, is_subscribed, description,
                   completed_interviews, conducted_interviews)
SELECT * FROM (
    VALUES
    (8881, 'Dev1', 'Rate1', 'Java Back-End', 'Ukraine', 'Dnipro', true, 'Java developer', 11, 8),
    (8882, 'Dev2', 'Rate2', 'ReactJS', 'Ukraine', 'Bila Tserkva', true, 'ReactJS developer', 13, 7),
    (8883, 'Dev3', 'Rate3', 'React+JAVA', 'Ukraine', 'Lviv', true, 'Full Stack developer', 9, 14)
) AS new_users (id, first_name, last_name, status, country, city, is_subscribed, description,
                completed_interviews, conducted_interviews)
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

--  Create specialization records
INSERT INTO specializations (id, name, completed_interviews, conducted_interviews, is_main, user_id)
SELECT *
FROM (VALUES (6661, 'Frontend Developer', 11, 4, true, 8881))
         AS specializations (id, name, completed_interviews, conducted_interviews, is_main, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM specializations
                  WHERE id = specializations.id);

--  Create masteries records
INSERT INTO masteries (id, level, soft_skill_mark, hard_skill_mark, specialization_id)
SELECT *
FROM (VALUES (10001, 1, 5.31, 6.73, 6661),
             (10002, 2, 8.12, 5.25, 6661),
             (10003, 3, 8.83, 9.46, 6661))
         AS masteries (id, level, soft_skill_mark, hard_skill_mark, specialization_id)
WHERE NOT EXISTS (SELECT 1
                  FROM masteries
                  WHERE id = masteries.id);

--  Create skills records
INSERT INTO skills (id, name, type, average_mark,counter, hide, is_grows, mastery_id)
SELECT *
FROM (VALUES  (100001, 'Communication', 'SOFT_SKILL', 5.31, 3, true, true, 10001),
              (100002, 'Problem-Solving', 'SOFT_SKILL', 8.12, 5, true, false, 10001),
              (100003, 'Adaptability', 'SOFT_SKILL', 9.83, 8, true, true, 10001),
              (100004, 'Critical Thinking', 'SOFT_SKILL', 8.83, 4, true, true, 10001),
              (100005, 'Time Management', 'SOFT_SKILL', 8.83, 9, true, true, 10001),
              (100006, 'Java Core', 'HARD_SKILL', 8.88, 9, true, true, 10001),
              (100007, 'Git Hub', 'HARD_SKILL', 8.38, 9, true, false, 10001),
              (100008, 'Hibernate', 'HARD_SKILL', 0, 0, false, true, 10001),
              (100009, 'Communication', 'SOFT_SKILL', 4.41, 3, false, true, 10002),
              (100010, 'Problem-Solving', 'SOFT_SKILL', 7.62, 5, false, false, 10002),
              (100011, 'Adaptability', 'SOFT_SKILL', 8.33, 7, false, true, 10002),
              (100012, 'Critical Thinking', 'SOFT_SKILL', 8.83, 5, false, true, 10002),
              (100013, 'Time Management', 'SOFT_SKILL', 9.13, 7, false, true, 10002),
              (100014, 'Java Core', 'HARD_SKILL', 7.11, 8, false, true, 10002),
              (100015, 'Git Hub', 'HARD_SKILL', 7.66, 9, false, false, 10002),
              (100016, 'Hibernate', 'HARD_SKILL', 0, 0, false, true, 10002),
              (100017, 'Communication', 'SOFT_SKILL', 5.55, 6, false, true, 10003),
              (100018, 'Problem-Solving', 'SOFT_SKILL', 9.02, 5, false, false, 10003),
              (100019, 'Adaptability', 'SOFT_SKILL', 7.33, 9, false, true, 10003),
              (100020, 'Critical Thinking', 'SOFT_SKILL', 6.63, 8, false, true, 10003),
              (100021, 'Time Management', 'SOFT_SKILL', 6.93, 7, false, true, 10003),
              (100022, 'Java Core', 'HARD_SKILL', 8.88, 9, false, true, 10003),
              (100023, 'Git Hub', 'HARD_SKILL', 9.18, 9, false, false, 10003),
              (100024, 'Hibernate', 'HARD_SKILL', 0, 0, false, true, 10003))
         AS skills (id, name, type, average_mark, counter, hide, is_grows, mastery_id)
WHERE NOT EXISTS (SELECT 1
                  FROM skills
                  WHERE id = skills.id);

-- Update specializations records
UPDATE specializations
SET main_mastery_id = 10001
WHERE id = 6661
  AND NOT EXISTS (SELECT 1
                  FROM specializations
                  WHERE id = 6661
                    AND main_mastery_id = 10001);