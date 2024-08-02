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
INSERT INTO skills (id, name, type, average_mark,counter, is_hidden, is_grows, mastery_id)
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
         AS skills (id, name, type, average_mark, counter, is_hidden, is_grows, mastery_id)
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

-- Insert mastery history records for 10 days and each month of the year
INSERT INTO mastery_histories (mastery_id, date, hard_skill_mark, soft_skill_mark)
VALUES
    -- 10 days
    (10001, '2024-07-19', 6.2, 5.7),
    (10001, '2024-07-20', 6.3, 5.8),
    (10001, '2024-07-21', 6.4, 5.9),
    (10001, '2024-07-22', 6.5, 6.0),
    (10001, '2024-07-23', 6.6, 6.1),
    (10001, '2024-07-24', 6.7, 6.2),
    (10001, '2024-07-25', 6.8, 6.3),
    (10001, '2024-07-26', 6.9, 6.4),
    (10001, '2024-07-27', 6.8, 6.3),
    (10001, '2024-07-28', 6.9, 6.4),
    (10001, '2024-07-29', 6.0, 5.5),
    (10001, '2024-07-30', 6.1, 5.6);

-- Insert mastery history records for each month of the year
INSERT INTO mastery_histories (mastery_id, date, hard_skill_mark, soft_skill_mark)
VALUES
    (10001, '2023-10-01', 7.0, 6.5),
    (10001, '2023-11-01', 7.5, 7.0),
    (10001, '2023-12-01', 8.0, 7.5),
    (10001, '2024-01-01', 8.5, 8.0),
    (10001, '2024-02-01', 9.0, 8.5),
    (10001, '2024-03-01', 8.0, 7.5),
    (10001, '2024-04-01', 7.5, 7.0),
    (10001, '2024-05-01', 7.0, 6.5),
    (10001, '2024-06-01', 6.5, 6.0),
    (10001, '2024-07-01', 6.0, 5.5),
    (10001, '2024-08-01', 6.5, 6.0);


-- Insert interview summaries for 10 days
INSERT INTO interview_summaries (id, date, duration, candidate_id, interviewer_id)
VALUES
    (1, '2024-07-21', 60, 8881, 8882),
    (2, '2024-07-21', 45, 8882, 8881),
    (3, '2024-07-23', 30, 8881, 8883),
    (4, '2024-07-23', 90, 8883, 8881),
    (5, '2024-07-23', 75, 8881, 8882),
    (6, '2024-07-24', 60, 8882, 8881),
    (7, '2024-07-25', 45, 8881, 8882),
    (8, '2024-07-26', 30, 8882, 8881),
    (9, '2024-07-26', 90, 8881, 8882),
    (10, '2024-07-27', 75, 8882, 8881),
    (11, '2024-07-27', 45, 8881, 8882),
    (12, '2024-07-27', 30, 8882, 8881),
    (13, '2024-07-28', 90, 8881, 8882),
    (14, '2024-07-28', 75, 8882, 8881);

-- Insert interview summaries for each month of the year
INSERT INTO interview_summaries (id, date, duration, candidate_id, interviewer_id)
VALUES
    (15, '2023-11-01', 60, 8881, 8882),
    (16, '2023-12-01', 45, 8882, 8881),
    (17, '2023-12-01', 30, 8881, 8882),
    (18, '2023-12-01', 90, 8882, 8881),
    (19, '2024-01-01', 75, 8881, 8882),
    (20, '2024-02-01', 60, 8882, 8881),
    (21, '2024-03-01', 45, 8881, 8882),
    (22, '2024-04-01', 30, 8882, 8881),
    (23, '2024-05-01', 90, 8881, 8882),
    (24, '2024-06-01', 75, 8882, 8881),
    (25, '2024-07-01', 60, 8881, 8882),
    (26, '2024-08-01', 45, 8882, 8881);

-- Associate interview summaries with user
-- Ensure the interview_summary_id exists in interview_summaries table
INSERT INTO interview_summaries_users (user_id, interview_summary_id)
VALUES
    (8881, 1), (8881, 2), (8881, 3), (8881, 4), (8881, 5),
    (8881, 6), (8881, 7), (8881, 8), (8881, 9), (8881, 10),
    (8881, 11), (8881, 12), (8881, 13), (8881, 14), (8881, 15),
    (8881, 16), (8881, 17), (8881, 18), (8881, 19), (8881, 20),
    (8881, 21), (8881, 22), (8881, 23), (8881, 24), (8881, 25),
    (8881, 26);




