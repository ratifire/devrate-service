--  Create user_security records
INSERT INTO users (id, first_name, last_name, status, country, city, is_subscribed, description,
                   completed_interviews, conducted_interviews, email, password)
SELECT * FROM (
    VALUES
    (8881, 'John', 'Rate', 'Java Back-End', 'Ukraine', 'Dnipro', true,
     'Dedicated and results-driven professional with extensive experience in software development and ' ||
     'project management. Proven track record in designing, implementing, and optimizing complex systems, ' ||
     'and leading cross-functional teams to achieve project goals.', 16, 18, 'john.rate3@tutamail.com', '$2a$12$jjNu2RoOrBhC3JNHyaO0yuNZX7Uqjrd8SprH4FAvCrqX8yDXfG1Wi'),
    (8882, 'Ratifire', 'First', 'ReactJS', 'Ukraine', 'Bila Tserkva', true,
     'Accomplished technology specialist with a robust background in software engineering and system architecture. ' ||
     'Expertise in developing scalable applications and integrating advanced technologies to drive ' ||
     'innovation and efficiency. Strong analytical and troubleshooting skills, with a history of successful ' ||
     'project delivery and client satisfaction.', 25, 9, 'dev.rate3@proton.me', '$2a$12$WFWGCRxsMF7rVSyTmzVRaeDdTvc3NKbH7xVNUxRvSUZpwvBHkWvmi')
) AS new_users (id, first_name, last_name, status, country, city, is_subscribed, description,
                completed_interviews, conducted_interviews, email, password)
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE id = new_users.id);

--  Create bookmarks records
INSERT INTO bookmarks (name, link, user_id)
SELECT * FROM (
VALUES
    ('Google bookmark', 'https://www.google.com/', 8881),
    ('Linkedin bookmark', 'https://www.linkedin.com/', 8881),
    ('Youtube bookmark', 'https://www.youtube.com/', 8881),
    ('Google bookmark', 'https://www.google.com/', 8882),
    ('Linkedin bookmark', 'https://www.linkedin.com/', 8882),
    ('Youtube bookmark', 'https://www.youtube.com/', 8882)
) AS bookmarks (name, link, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM bookmarks
                  WHERE id = bookmarks.id);

--  Create achievement records
INSERT INTO achievements (summary, description, user_id)
SELECT * FROM (
VALUES
    ('Best Developer Award 2023', 'Awarded for outstanding performance and contribution to the development team in 2023.', 8881),
    ('Top Innovator Award', 'Recognized for innovative solutions and creative problem-solving skills.', 8881),
    ('Excellence in Project Management', 'Honored for exceptional skills in managing and delivering projects on time and within budget.', 8881),
    ('Employee of the Month', 'Awarded for exceptional work and dedication during the month of June.', 8882),
    ('Outstanding Contribution to Open Source', 'Acknowledged for significant contributions to various open-source projects.', 8882)
) AS achievements (summary, description, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM achievements
                  WHERE id = achievements.id);

--  Create educations records
INSERT INTO educations (type, name, description, start_year, end_year, user_id)
SELECT * FROM (
VALUES
    ('Schoolboy', 'School #1', 'School degree.', 2000, 2010, 8881),
    ('Computer Science', 'Institute #2', 'Bachelor’s degree in Computer Science.', 2010, 2014, 8881),
    ('Software Engineering', 'Institute #2', 'Master’s degree in Software Engineering.', 2015, 2017, 8881),
    ('Artificial Intelligence', 'Institute #2', 'PhD in Artificial Intelligence.', 2018, 2022, 8881),
    ('Schoolboy', 'School #2', 'School degree.', 2000, 2011, 8882),
    ('Web Development', 'Institute #3', 'Diploma in Web Development.', 2011, 2015, 8882),
    ('Cyber Security', 'Some place #4', 'Master’s degree in Cyber Security.', 2015, 2017, 8882),
    ('Cloud Computing', 'High school', 'Certification in Cloud Computing.', 2018, 2020, 8882)
) AS educations (type, name, description, start_year, end_year, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM educations
                  WHERE id = educations.id);

--  Create bookmarks records
INSERT INTO contacts (type, value, user_id)
SELECT * FROM (
VALUES
    ('EMAIL', 'john.rate3@tutamail.com', 8881),
    ('PHONE_NUMBER', '+11234567890', 8881),
    ('TELEGRAM_LINK', 'https://t.me/johnRate', 8881),
    ('LINKEDIN_LINK', 'https://www.linkedin.com/in/john-rate', 8881),
    ('GITHUB_LINK', 'https://github.com/john-rate', 8881),
    ('BEHANCE_LINK', 'https://www.behance.net/john-rate', 8881),
    ('EMAIL', 'dev.rate3@proton.me', 8882),
    ('PHONE_NUMBER', '+11234565874', 8882),
    ('TELEGRAM_LINK', 'https://t.me/devRate', 8882),
    ('LINKEDIN_LINK', 'https://www.linkedin.com/in/dev-rate', 8882),
    ('GITHUB_LINK', 'https://github.com/dev-rate', 8882),
    ('BEHANCE_LINK', 'https://www.behance.net/dev-rate', 8882)
) AS contacts (type, value, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM contacts
                  WHERE id = contacts.id);

--  Create language_proficiencies records
INSERT INTO language_proficiencies (name, code, level, user_id)
SELECT * FROM (
VALUES
    ('en', 'en', 'b1', 8881),
    ('ua', 'ua', 'c2', 8881),
    ('fr', 'fr', 'a2', 8881),
    ('de', 'de', 'b2', 8881),
    ('en', 'en', 'b1', 8882),
    ('ua', 'ua', 'c2', 8882),
    ('fr', 'fr', 'a2', 8882),
    ('de', 'de', 'b2', 8882)
) AS language_proficiencies (name, code, level, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM language_proficiencies
                  WHERE id = language_proficiencies.id);

--  Create employment_records records
INSERT INTO employment_records (start_year, end_year, position, company_name, description, responsibilities, user_id)
SELECT * FROM (
VALUES
    (2021, 2022, 'Software Engineer', 'Tech Corp', 'Developed and maintained software applications.',
     ARRAY['Developed new features', 'Maintained codebase', 'Collaborated with team'], 8881),
    (2019, 2021, 'Frontend Developer', 'Web Solutions Inc.', 'Designed and implemented user interfaces.',
     ARRAY['Designed UI layouts', 'Implemented responsive designs', 'Conducted user testing'], 8881),
    (2018, 2021, 'Intern', 'StartUp Ltd.', 'Assisted with various tasks in the development team.',
     ARRAY['Assisted with code reviews', 'Wrote documentation', 'Participated in team meetings'], 8881),
    (2020, 2023, 'Data Scientist', 'Data Analytics LLC', 'Analyzed and interpreted complex data to help companies make informed decisions.',
     ARRAY['Developed data models', 'Performed statistical analysis', 'Created visualizations'], 8882),
    (2017, 2020, 'Marketing Manager', 'Creative Agency', 'Led marketing campaigns and strategies to drive brand growth and engagement.',
     ARRAY['Managed marketing campaigns', 'Coordinated with external agencies', 'Analyzed campaign performance'], 8882),
    (2015, 2017, 'Sales Associate', 'Retail World', 'Provided customer service and supported sales operations in a retail environment.',
     ARRAY['Assisted customers', 'Processed sales transactions', 'Maintained store inventory'], 8882)
) AS employment_records (start_year, end_year, position, company_name, description, responsibilities, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM employment_records
                  WHERE id = employment_records.id);

--  Create specialization records
INSERT INTO specializations (id, name, completed_interviews, conducted_interviews, is_main, user_id)
SELECT * FROM (
VALUES
    (6661, 'Frontend Developer', 11, 4, true, 8881),
    (6662, 'Backend Developer', 5, 14, false, 8881),
    (6663, 'Frontend Developer', 10, 6, true, 8882),
    (6664, 'Backend Developer', 15, 3, false, 8882)
) AS specializations (id, name, completed_interviews, conducted_interviews, is_main, user_id)
WHERE NOT EXISTS (SELECT 1
                  FROM specializations
                  WHERE id = specializations.id);

--  Create masteries records
INSERT INTO masteries (id, level, soft_skill_mark, hard_skill_mark, specialization_id)
SELECT * FROM (
VALUES
    (10001, 1, 8.18, 6.35, 6661),
    (10002, 2, 7.66, 4.92, 6661),
    (10003, 3, 7.09, 6.02, 6661),
    (10004, 1, 7.25, 7.50, 6662),
    (10005, 2, 7.91, 7.61, 6662),
    (10006, 3, 7.47, 7.90, 6662),
    (10007, 1, 7.91, 7.75, 6663),
    (10008, 2, 7.92, 8.07, 6663),
    (10009, 3, 7.38, 7.78, 6663),
    (10010, 1, 7.40, 8.07, 6664),
    (10011, 2, 7.89, 8.11, 6664),
    (10012, 3, 7.89, 8.23, 6664)
) AS masteries (id, level, soft_skill_mark, hard_skill_mark, specialization_id)
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

                -- New skills required for front-end, testing search function
              (100097, 'jazz', 'HARD_SKILL', 7.5, 5, true, true, 10001),
              (100098, 'quick', 'HARD_SKILL', 6.0, 3, true, false, 10001),
              (100099, 'brown', 'HARD_SKILL', 5.2, 4, true, false, 10001),
              (100100, 'fox', 'HARD_SKILL', 8.0, 2, true, true, 10001),
              (100101, 'jumps', 'HARD_SKILL', 7.0, 6, true, false, 10001),
              (100102, 'over', 'HARD_SKILL', 4.5, 1, true, true, 10001),
              (100103, 'lazy', 'HARD_SKILL', 6.8, 3, true, false, 10001),
              (100104, 'dog', 'HARD_SKILL', 9.0, 8, true, true, 10001),
              (100105, 'quiz', 'HARD_SKILL', 5.5, 2, true, true, 10001),
              (100106, 'vex', 'HARD_SKILL', 6.3, 3, true, true, 10001),
              (100107, 'whack', 'HARD_SKILL', 4.8, 1, true, true, 10001),
              (100108, 'fjord', 'HARD_SKILL', 7.2, 6, true, false, 10001),
              (100109, 'blitz', 'HARD_SKILL', 6.7, 5, true, true, 10001),
              (100110, 'gaze', 'HARD_SKILL', 8.5, 7, true, true, 10001),
              (100111, 'pique', 'HARD_SKILL', 5.0, 2, true, false, 10001),
              (100112, 'zebra', 'HARD_SKILL', 7.3, 4, true, false, 10001),
              (100113, 'jerk', 'HARD_SKILL', 5.8, 3, true, false, 10001),
              (100114, 'knight', 'HARD_SKILL', 6.5, 2, true, true, 10001),
              (100115, 'quiver', 'HARD_SKILL', 4.2, 1, true, false, 10001),
              (100116, 'wax', 'HARD_SKILL', 7.0, 4, true, true, 10001),

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
              (100024, 'Hibernate', 'HARD_SKILL', 0, 0, false, true, 10003),
              (100025, 'Leadership', 'SOFT_SKILL', 6.54, 4, false, true, 10004),
              (100026, 'Project Management', 'SOFT_SKILL', 7.88, 5, true, true, 10004),
              (100027, 'Team Collaboration', 'SOFT_SKILL', 8.75, 6, false, true, 10004),
              (100028, 'Negotiation', 'SOFT_SKILL', 5.99, 3, true, false, 10004),
              (100029, 'Public Speaking', 'SOFT_SKILL', 7.11, 4, true, true, 10004),
              (100030, 'SQL', 'HARD_SKILL', 8.21, 7, false, true, 10004),
              (100031, 'Docker', 'HARD_SKILL', 6.85, 5, true, false, 10004),
              (100032, 'Kubernetes', 'HARD_SKILL', 7.45, 6, true, true, 10004),
              (100033, 'Leadership', 'SOFT_SKILL', 7.23, 4, false, true, 10005),
              (100034, 'Project Management', 'SOFT_SKILL', 8.67, 5, true, true, 10005),
              (100035, 'Team Collaboration', 'SOFT_SKILL', 9.12, 6, true, true, 10005),
              (100036, 'Negotiation', 'SOFT_SKILL', 6.22, 3, false, false, 10005),
              (100037, 'Public Speaking', 'SOFT_SKILL', 8.33, 5, true, true, 10005),
              (100038, 'SQL', 'HARD_SKILL', 7.77, 6, false, true, 10005),
              (100039, 'Docker', 'HARD_SKILL', 8.09, 7, true, true, 10005),
              (100040, 'Kubernetes', 'HARD_SKILL', 6.98, 5, true, false, 10005),
              (100041, 'Leadership', 'SOFT_SKILL', 8.44, 5, true, true, 10006),
              (100042, 'Project Management', 'SOFT_SKILL', 6.78, 4, false, true, 10006),
              (100043, 'Team Collaboration', 'SOFT_SKILL', 7.89, 6, true, true, 10006),
              (100044, 'Negotiation', 'SOFT_SKILL', 6.67, 4, true, false, 10006),
              (100045, 'Public Speaking', 'SOFT_SKILL', 7.55, 4, false, true, 10006),
              (100046, 'SQL', 'HARD_SKILL', 8.12, 7, true, true, 10006),
              (100047, 'Docker', 'HARD_SKILL', 7.68, 6, false, true, 10006),
              (100048, 'Kubernetes', 'HARD_SKILL', 7.89, 6, true, true, 10006),
              (100049, 'Leadership', 'SOFT_SKILL', 7.56, 5, true, true, 10007),
              (100050, 'Project Management', 'SOFT_SKILL', 8.78, 6, true, true, 10007),
              (100051, 'Team Collaboration', 'SOFT_SKILL', 8.34, 5, true, true, 10007),
              (100052, 'Negotiation', 'SOFT_SKILL', 7.88, 4, false, true, 10007),
              (100053, 'Public Speaking', 'SOFT_SKILL', 6.99, 5, true, false, 10007),
              (100054, 'SQL', 'HARD_SKILL', 8.54, 8, true, true, 10007),
              (100055, 'Docker', 'HARD_SKILL', 7.93, 7, true, true, 10007),
              (100056, 'Kubernetes', 'HARD_SKILL', 6.78, 6, false, true, 10007),
              (100057, 'Leadership', 'SOFT_SKILL', 8.22, 4, true, true, 10008),
              (100058, 'Project Management', 'SOFT_SKILL', 7.93, 5, true, true, 10008),
              (100059, 'Team Collaboration', 'SOFT_SKILL', 9.12, 6, false, true, 10008),
              (100060, 'Negotiation', 'SOFT_SKILL', 6.56, 4, false, false, 10008),
              (100061, 'Public Speaking', 'SOFT_SKILL', 7.76, 5, true, true, 10008),
              (100062, 'SQL', 'HARD_SKILL', 8.34, 7, true, true, 10008),
              (100063, 'Docker', 'HARD_SKILL', 7.85, 6, false, true, 10008),
              (100064, 'Kubernetes', 'HARD_SKILL', 8.01, 7, true, true, 10008),
              (100065, 'Leadership', 'SOFT_SKILL', 7.89, 5, true, true, 10009),
              (100066, 'Project Management', 'SOFT_SKILL', 6.72, 4, false, true, 10009),
              (100067, 'Team Collaboration', 'SOFT_SKILL', 8.14, 6, true, true, 10009),
              (100068, 'Negotiation', 'SOFT_SKILL', 6.23, 4, true, true, 10009),
              (100069, 'Public Speaking', 'SOFT_SKILL', 7.90, 5, true, false, 10009),
              (100070, 'SQL', 'HARD_SKILL', 7.99, 6, true, true, 10009),
              (100071, 'Docker', 'HARD_SKILL', 8.45, 7, false, true, 10009),
              (100072, 'Kubernetes', 'HARD_SKILL', 6.90, 5, true, true, 10009),
              (100073, 'Leadership', 'SOFT_SKILL', 7.65, 5, true, true, 10010),
              (100074, 'Project Management', 'SOFT_SKILL', 6.85, 4, true, true, 10010),
              (100075, 'Team Collaboration', 'SOFT_SKILL', 8.12, 6, false, true, 10010),
              (100076, 'Negotiation', 'SOFT_SKILL', 6.98, 3, false, false, 10010),
              (100077, 'Public Speaking', 'SOFT_SKILL', 7.42, 5, true, true, 10010),
              (100078, 'SQL', 'HARD_SKILL', 8.27, 7, true, true, 10010),
              (100079, 'Docker', 'HARD_SKILL', 7.88, 6, false, true, 10010),
              (100080, 'Kubernetes', 'HARD_SKILL', 8.05, 6, true, true, 10010),
              (100081, 'Leadership', 'SOFT_SKILL', 8.34, 5, true, true, 10011),
              (100082, 'Project Management', 'SOFT_SKILL', 7.56, 4, true, true, 10011),
              (100083, 'Team Collaboration', 'SOFT_SKILL', 9.01, 6, false, true, 10011),
              (100084, 'Negotiation', 'SOFT_SKILL', 6.44, 4, false, true, 10011),
              (100085, 'Public Speaking', 'SOFT_SKILL', 8.12, 5, true, true, 10011),
              (100086, 'SQL', 'HARD_SKILL', 8.43, 8, true, true, 10011),
              (100087, 'Docker', 'HARD_SKILL', 7.67, 6, true, false, 10011),
              (100088, 'Kubernetes', 'HARD_SKILL', 8.22, 7, true, true, 10011),
              (100089, 'Leadership', 'SOFT_SKILL', 7.78, 4, true, true, 10012),
              (100090, 'Project Management', 'SOFT_SKILL', 8.99, 5, true, true, 10012),
              (100091, 'Team Collaboration', 'SOFT_SKILL', 8.45, 6, false, true, 10012),
              (100092, 'Negotiation', 'SOFT_SKILL', 6.89, 4, true, true, 10012),
              (100093, 'Public Speaking', 'SOFT_SKILL', 7.32, 5, true, false, 10012),
              (100094, 'SQL', 'HARD_SKILL', 8.56, 7, true, true, 10012),
              (100095, 'Docker', 'HARD_SKILL', 7.79, 6, false, true, 10012),
              (100096, 'Kubernetes', 'HARD_SKILL', 8.34, 8, true, true, 10012)
) AS skills (id, name, type, average_mark, counter, is_hidden, is_grows, mastery_id)
WHERE NOT EXISTS (SELECT 1
                  FROM skills
                  WHERE id = skills.id);

-- Update specializations records
UPDATE specializations
SET main_mastery_id = 10001
WHERE id = 6661
  AND NOT EXISTS (SELECT 1 FROM specializations
                  WHERE id = 6661 AND main_mastery_id = 10001);

UPDATE specializations
SET main_mastery_id = 10005
WHERE id = 6662
  AND NOT EXISTS (SELECT 1 FROM specializations
                  WHERE id = 6662 AND main_mastery_id = 10005);

UPDATE specializations
SET main_mastery_id = 10009
WHERE id = 6663
  AND NOT EXISTS (SELECT 1 FROM specializations
                  WHERE id = 6663 AND main_mastery_id = 10009);

UPDATE specializations
SET main_mastery_id = 10010
WHERE id = 6664
  AND NOT EXISTS (SELECT 1 FROM specializations
                  WHERE id = 6664 AND main_mastery_id = 10010);

-- Insert mastery history records for 10 days and each month of the year
INSERT INTO mastery_histories (mastery_id, date, hard_skill_mark, soft_skill_mark)
VALUES
    -- July
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
    (10001, '2024-07-30', 6.1, 5.6),
    -- August
    (10002, '2024-08-01', 7.1, 6.0),
    (10002, '2024-08-02', 7.2, 6.1),
    (10002, '2024-08-03', 7.3, 6.2),
    (10002, '2024-08-04', 7.4, 6.3),
    (10002, '2024-08-05', 7.5, 6.4),
    (10002, '2024-08-06', 7.6, 6.5),
    (10002, '2024-08-07', 7.7, 6.6),
    (10002, '2024-08-08', 7.8, 6.7),
    (10002, '2024-08-09', 7.9, 6.8),
    (10002, '2024-08-10', 8.0, 6.9),
    -- September
    (10003, '2024-09-01', 8.1, 7.0),
    (10003, '2024-09-02', 8.2, 7.1),
    (10003, '2024-09-03', 8.3, 7.2),
    (10003, '2024-09-04', 8.4, 7.3),
    (10003, '2024-09-05', 8.5, 7.4),
    (10003, '2024-09-06', 8.6, 7.5),
    (10003, '2024-09-07', 8.7, 7.6),
    (10003, '2024-09-08', 8.8, 7.7),
    (10003, '2024-09-09', 8.9, 7.8),
    (10003, '2024-09-10', 9.0, 7.9),
    -- October
    (10004, '2024-10-01', 5.5, 4.6),
    (10004, '2024-10-02', 5.6, 4.7),
    (10004, '2024-10-03', 5.7, 4.8),
    (10004, '2024-10-04', 5.8, 4.9),
    (10004, '2024-10-05', 5.9, 5.0),
    (10004, '2024-10-06', 6.0, 5.1),
    (10004, '2024-10-07', 6.1, 5.2),
    (10004, '2024-10-08', 6.2, 5.3),
    (10004, '2024-10-09', 6.3, 5.4),
    (10004, '2024-10-10', 6.4, 5.5),
    -- November
    (10005, '2024-11-01', 7.2, 6.3),
    (10005, '2024-11-02', 7.3, 6.4),
    (10005, '2024-11-03', 7.4, 6.5),
    (10005, '2024-11-04', 7.5, 6.6),
    (10005, '2024-11-05', 7.6, 6.7),
    (10005, '2024-11-06', 7.7, 6.8),
    (10005, '2024-11-07', 7.8, 6.9),
    (10005, '2024-11-08', 7.9, 7.0),
    (10005, '2024-11-09', 8.0, 7.1),
    (10005, '2024-11-10', 8.1, 7.2),
    -- December
    (10006, '2024-12-01', 6.7, 5.8),
    (10006, '2024-12-02', 6.8, 5.9),
    (10006, '2024-12-03', 6.9, 6.0),
    (10006, '2024-12-04', 7.0, 6.1),
    (10006, '2024-12-05', 7.1, 6.2),
    (10006, '2024-12-06', 7.2, 6.3),
    (10006, '2024-12-07', 7.3, 6.4),
    (10006, '2024-12-08', 7.4, 6.5),
    (10006, '2024-12-09', 7.5, 6.6),
    (10006, '2024-12-10', 7.6, 6.7),
    -- July
    (10006, '2024-07-19', 6.7, 5.8),
    (10006, '2024-07-20', 6.8, 5.9),
    (10006, '2024-07-21', 6.9, 6.0),
    (10006, '2024-07-22', 7.0, 6.1),
    (10006, '2024-07-23', 7.1, 6.2),
    (10006, '2024-07-24', 7.2, 6.3),
    (10006, '2024-07-25', 7.3, 6.4),
    (10006, '2024-07-26', 7.4, 6.5),
    (10006, '2024-07-27', 7.5, 6.6),
    (10006, '2024-07-28', 7.6, 6.7),
    (10006, '2024-07-29', 7.5, 6.6),
    (10006, '2024-07-30', 7.4, 6.5),
    -- August
    (10007, '2024-08-01', 5.6, 4.8),
    (10007, '2024-08-02', 5.7, 4.9),
    (10007, '2024-08-03', 5.8, 5.0),
    (10007, '2024-08-04', 5.9, 5.1),
    (10007, '2024-08-05', 6.0, 5.2),
    (10007, '2024-08-06', 6.1, 5.3),
    (10007, '2024-08-07', 6.2, 5.4),
    (10007, '2024-08-08', 6.3, 5.5),
    (10007, '2024-08-09', 6.4, 5.6),
    (10007, '2024-08-10', 6.5, 5.7),
    -- September
    (10008, '2024-09-01', 7.3, 6.4),
    (10008, '2024-09-02', 7.4, 6.5),
    (10008, '2024-09-03', 7.5, 6.6),
    (10008, '2024-09-04', 7.6, 6.7),
    (10008, '2024-09-05', 7.7, 6.8),
    (10008, '2024-09-06', 7.8, 6.9),
    (10008, '2024-09-07', 7.9, 7.0),
    (10008, '2024-09-08', 8.0, 7.1),
    (10008, '2024-09-09', 8.1, 7.2),
    (10008, '2024-09-10', 8.2, 7.3),
    -- October
    (10009, '2024-10-01', 6.8, 5.9),
    (10009, '2024-10-02', 6.9, 6.0),
    (10009, '2024-10-03', 7.0, 6.1),
    (10009, '2024-10-04', 7.1, 6.2),
    (10009, '2024-10-05', 7.2, 6.3),
    (10009, '2024-10-06', 7.3, 6.4),
    (10009, '2024-10-07', 7.4, 6.5),
    (10009, '2024-10-08', 7.5, 6.6),
    (10009, '2024-10-09', 7.6, 6.7),
    (10009, '2024-10-10', 7.7, 6.8),
    -- November
    (10010, '2024-11-01', 8.0, 7.1),
    (10010, '2024-11-02', 8.1, 7.2),
    (10010, '2024-11-03', 8.2, 7.3),
    (10010, '2024-11-04', 8.3, 7.4),
    (10010, '2024-11-05', 8.4, 7.5),
    (10010, '2024-11-06', 8.5, 7.6),
    (10010, '2024-11-07', 8.6, 7.7),
    (10010, '2024-11-08', 8.7, 7.8),
    (10010, '2024-11-09', 8.8, 7.9),
    (10010, '2024-11-10', 8.9, 8.0),
    -- December
    (10011, '2024-12-01', 5.8, 4.9),
    (10011, '2024-12-02', 5.9, 5.0),
    (10011, '2024-12-03', 6.0, 5.1),
    (10011, '2024-12-04', 6.1, 5.2),
    (10011, '2024-12-05', 6.2, 5.3),
    (10011, '2024-12-06', 6.3, 5.4),
    (10011, '2024-12-07', 6.4, 5.5),
    (10011, '2024-12-08', 6.5, 5.6),
    (10011, '2024-12-09', 6.6, 5.7),
    (10011, '2024-12-10', 6.7, 5.8),
    -- January
    (10012, '2024-01-01', 7.1, 6.2),
    (10012, '2024-01-02', 7.2, 6.3),
    (10012, '2024-01-03', 7.3, 6.4),
    (10012, '2024-01-04', 7.4, 6.5),
    (10012, '2024-01-05', 7.5, 6.6),
    (10012, '2024-01-06', 7.6, 6.7),
    (10012, '2024-01-07', 7.7, 6.8),
    (10012, '2024-01-08', 7.8, 6.9),
    (10012, '2024-01-09', 7.9, 7.0),
    (10012, '2024-01-10', 8.0, 7.1)
ON CONFLICT (id) DO NOTHING;

-- Insert mastery history records for each month of the year
INSERT INTO mastery_histories (mastery_id, date, hard_skill_mark, soft_skill_mark)
VALUES
    -- For mastery_id 10001
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
    (10001, '2024-08-01', 6.5, 6.0),
    -- For mastery_id 10002
    (10002, '2023-10-01', 7.2, 6.3),
    (10002, '2023-11-01', 7.4, 6.5),
    (10002, '2023-12-01', 7.6, 6.7),
    (10002, '2024-01-01', 7.8, 6.9),
    (10002, '2024-02-01', 8.0, 7.1),
    (10002, '2024-03-01', 7.8, 6.9),
    (10002, '2024-04-01', 7.6, 6.7),
    (10002, '2024-05-01', 7.4, 6.5),
    (10002, '2024-06-01', 7.2, 6.3),
    (10002, '2024-07-01', 7.0, 6.1),
    (10002, '2024-08-01', 7.2, 6.3),
    -- For mastery_id 10003
    (10003, '2023-10-01', 8.1, 7.2),
    (10003, '2023-11-01', 8.3, 7.4),
    (10003, '2023-12-01', 8.5, 7.6),
    (10003, '2024-01-01', 8.7, 7.8),
    (10003, '2024-02-01', 8.9, 8.0),
    (10003, '2024-03-01', 8.7, 7.8),
    (10003, '2024-04-01', 8.5, 7.6),
    (10003, '2024-05-01', 8.3, 7.4),
    (10003, '2024-06-01', 8.1, 7.2),
    (10003, '2024-07-01', 7.9, 7.0),
    (10003, '2024-08-01', 8.1, 7.2),
    -- For mastery_id 10004
    (10004, '2023-10-01', 6.9, 5.8),
    (10004, '2023-11-01', 7.1, 6.0),
    (10004, '2023-12-01', 7.3, 6.2),
    (10004, '2024-01-01', 7.5, 6.4),
    (10004, '2024-02-01', 7.7, 6.6),
    (10004, '2024-03-01', 7.5, 6.4),
    (10004, '2024-04-01', 7.3, 6.2),
    (10004, '2024-05-01', 7.1, 6.0),
    (10004, '2024-06-01', 6.9, 5.8),
    (10004, '2024-07-01', 6.7, 5.6),
    (10004, '2024-08-01', 6.9, 5.8),
    -- For mastery_id 10005
    (10005, '2023-10-01', 7.4, 6.5),
    (10005, '2023-11-01', 7.6, 6.7),
    (10005, '2023-12-01', 7.8, 6.9),
    (10005, '2024-01-01', 8.0, 7.1),
    (10005, '2024-02-01', 8.2, 7.3),
    (10005, '2024-03-01', 8.0, 7.1),
    (10005, '2024-04-01', 7.8, 6.9),
    (10005, '2024-05-01', 7.6, 6.7),
    (10005, '2024-06-01', 7.4, 6.5),
    (10005, '2024-07-01', 7.2, 6.3),
    (10005, '2024-08-01', 7.4, 6.5),
    -- For mastery_id 10006
    (10006, '2023-10-01', 6.8, 5.9),
    (10006, '2023-11-01', 7.0, 6.1),
    (10006, '2023-12-01', 7.2, 6.3),
    (10006, '2024-01-01', 7.4, 6.5),
    (10006, '2024-02-01', 7.6, 6.7),
    (10006, '2024-03-01', 7.4, 6.5),
    (10006, '2024-04-01', 7.2, 6.3),
    (10006, '2024-05-01', 7.0, 6.1),
    (10006, '2024-06-01', 6.8, 5.9),
    (10006, '2024-07-01', 6.6, 5.7),
    (10006, '2024-08-01', 6.8, 5.9),
    -- For mastery_id 10007
    (10007, '2023-10-01', 5.9, 4.9),
    (10007, '2023-11-01', 6.1, 5.1),
    (10007, '2023-12-01', 6.3, 5.3),
    (10007, '2024-01-01', 6.5, 5.5),
    (10007, '2024-02-01', 6.7, 5.7),
    (10007, '2024-03-01', 6.5, 5.5),
    (10007, '2024-04-01', 6.3, 5.3),
    (10007, '2024-05-01', 6.1, 5.1),
    (10007, '2024-06-01', 5.9, 4.9),
    (10007, '2024-07-01', 5.7, 4.7),
    (10007, '2024-08-01', 5.9, 4.9),
    -- For mastery_id 10008
    (10008, '2023-10-01', 7.2, 6.3),
    (10008, '2023-11-01', 7.4, 6.5),
    (10008, '2023-12-01', 7.6, 6.7),
    (10008, '2024-01-01', 7.8, 6.9),
    (10008, '2024-02-01', 8.0, 7.1),
    (10008, '2024-03-01', 7.8, 6.9),
    (10008, '2024-04-01', 7.6, 6.7),
    (10008, '2024-05-01', 7.4, 6.5),
    (10008, '2024-06-01', 7.2, 6.3),
    (10008, '2024-07-01', 7.0, 6.1),
    (10008, '2024-08-01', 7.2, 6.3),
    -- For mastery_id 10009
    (10009, '2023-10-01', 6.5, 5.6),
    (10009, '2023-11-01', 6.7, 5.8),
    (10009, '2023-12-01', 6.9, 6.0),
    (10009, '2024-01-01', 7.1, 6.2),
    (10009, '2024-02-01', 7.3, 6.4),
    (10009, '2024-03-01', 7.1, 6.2),
    (10009, '2024-04-01', 6.9, 6.0),
    (10009, '2024-05-01', 6.7, 5.8),
    (10009, '2024-06-01', 6.5, 5.6),
    (10009, '2024-07-01', 6.3, 5.4),
    (10009, '2024-08-01', 6.5, 5.6),
    -- For mastery_id 10010
    (10010, '2023-10-01', 8.1, 7.2),
    (10010, '2023-11-01', 8.3, 7.4),
    (10010, '2023-12-01', 8.5, 7.6),
    (10010, '2024-01-01', 8.7, 7.8),
    (10010, '2024-02-01', 8.9, 8.0),
    (10010, '2024-03-01', 8.7, 7.8),
    (10010, '2024-04-01', 8.5, 7.6),
    (10010, '2024-05-01', 8.3, 7.4),
    (10010, '2024-06-01', 8.1, 7.2),
    (10010, '2024-07-01', 7.9, 7.0),
    (10010, '2024-08-01', 8.1, 7.2),
    -- For mastery_id 10011
    (10011, '2023-10-01', 5.6, 4.7),
    (10011, '2023-11-01', 5.8, 4.9),
    (10011, '2023-12-01', 6.0, 5.1),
    (10011, '2024-01-01', 6.2, 5.3),
    (10011, '2024-02-01', 6.4, 5.5),
    (10011, '2024-03-01', 6.2, 5.3),
    (10011, '2024-04-01', 6.0, 5.1),
    (10011, '2024-05-01', 5.8, 4.9),
    (10011, '2024-06-01', 5.6, 4.7),
    (10011, '2024-07-01', 5.4, 4.5),
    (10011, '2024-08-01', 5.6, 4.7),
    -- For mastery_id 10012
    (10012, '2023-10-01', 7.4, 6.5),
    (10012, '2023-11-01', 7.6, 6.7),
    (10012, '2023-12-01', 7.8, 6.9),
    (10012, '2024-01-01', 8.0, 7.1),
    (10012, '2024-02-01', 8.2, 7.3),
    (10012, '2024-03-01', 8.0, 7.1),
    (10012, '2024-04-01', 7.8, 6.9),
    (10012, '2024-05-01', 7.6, 6.7),
    (10012, '2024-06-01', 7.4, 6.5),
    (10012, '2024-07-01', 7.2, 6.3),
    (10012, '2024-08-01', 7.4, 6.5)
ON CONFLICT (id) DO NOTHING;

-- Insert interview summaries for 10 days
INSERT INTO interview_summaries (id, date, duration, candidate_id, interviewer_id)
VALUES
    (1000, '2024-07-21', 60, 8881, 8882),
    (2000, '2024-07-21', 45, 8882, 8881),
    (5000, '2024-07-23', 75, 8881, 8882),
    (6000, '2024-07-24', 60, 8882, 8881),
    (7000, '2024-07-25', 45, 8881, 8882),
    (8000, '2024-07-26', 30, 8882, 8881),
    (9000, '2024-07-26', 90, 8881, 8882),
    (10000, '2024-07-27', 75, 8882, 8881),
    (11000, '2024-07-27', 45, 8881, 8882),
    (12000, '2024-07-27', 30, 8882, 8881),
    (13000, '2024-07-28', 90, 8881, 8882),
    (14000, '2024-07-28', 75, 8882, 8881)
ON CONFLICT (id) DO NOTHING;

-- Insert interview summaries for each month of the year
INSERT INTO interview_summaries (id, date, duration, candidate_id, interviewer_id)
VALUES
    (15000, '2023-11-01', 60, 8881, 8882),
    (16000, '2023-12-01', 45, 8882, 8881),
    (17000, '2023-12-01', 30, 8881, 8882),
    (18000, '2023-12-01', 90, 8882, 8881),
    (19000, '2024-01-01', 75, 8881, 8882),
    (20000, '2024-02-01', 60, 8882, 8881),
    (21000, '2024-03-01', 45, 8881, 8882),
    (22000, '2024-04-01', 30, 8882, 8881),
    (23000, '2024-05-01', 90, 8881, 8882),
    (24000, '2024-06-01', 75, 8882, 8881),
    (25000, '2024-07-01', 60, 8881, 8882),
    (26000, '2024-08-01', 45, 8882, 8881)
ON CONFLICT (id) DO NOTHING;

-- Associate interview summaries with user
-- Ensure the interview_summary_id exists in interview_summaries table
INSERT INTO interview_summaries_users (user_id, interview_summary_id)
VALUES
    (8881, 1000), (8881, 2000), (8881, 5000), (8881, 6000), (8881, 7000),
    (8881, 8000), (8881, 9000), (8881, 10000), (8881, 11000), (8881, 12000),
    (8881, 13000), (8881, 14000), (8881, 15000), (8881, 16000), (8881, 17000),
    (8881, 18000), (8881, 19000), (8881, 20000), (8881, 21000), (8881, 22000),
    (8881, 23000), (8881, 24000), (8881, 25000), (8881, 26000);

--  Create event records
INSERT INTO events (id, event_type_id, type, room_link, host_id, participant_id, start_time)
SELECT * FROM (
VALUES
    (10001, 20001, 'INTERVIEW', 'https://zoom.us/j/123456701', 8881, ARRAY[8882], '2024-09-02T00:00:00Z'::timestamptz),
    (10002, 20002, 'INTERVIEW', 'https://zoom.us/j/123456702', 8881, ARRAY[8882], '2024-09-04T12:00:00Z'::timestamptz),
    (10003, 20003, 'INTERVIEW', 'https://zoom.us/j/123456703', 8881, ARRAY[8882], '2024-09-07T19:00:00Z'::timestamptz),
    (10004, 20004, 'INTERVIEW', 'https://zoom.us/j/123456704', 8881, ARRAY[8882], '2024-09-09T01:00:00Z'::timestamptz),
    (10005, 20005, 'INTERVIEW', 'https://zoom.us/j/123456705', 8881, ARRAY[8882], '2024-09-11T11:00:00Z'::timestamptz),
    (10006, 20006, 'INTERVIEW', 'https://zoom.us/j/123456706', 8881, ARRAY[8882], '2024-09-13T20:00:00Z'::timestamptz),
    (10007, 20007, 'INTERVIEW', 'https://zoom.us/j/123456707', 8881, ARRAY[8882], '2024-09-24T00:00:00Z'::timestamptz),
    (10008, 20008, 'INTERVIEW', 'https://zoom.us/j/123456708', 8881, ARRAY[8882], '2024-09-26T10:00:00Z'::timestamptz),
    (10009, 20009, 'INTERVIEW', 'https://zoom.us/j/123456709', 8881, ARRAY[8882], '2024-09-28T19:00:00Z'::timestamptz)
) AS events (id, event_type_id, type, room_link, host_id, participant_id, start_time)
WHERE NOT EXISTS (SELECT 1
                  FROM events
                  WHERE id = events.id);

-- Create user-event relation records
INSERT INTO user_event (user_id, event_id)
SELECT * FROM (
VALUES
    (8881, 10001),
    (8882, 10001),
    (8881, 10002),
    (8882, 10002),
    (8881, 10003),
    (8882, 10003),
    (8881, 10004),
    (8882, 10004),
    (8881, 10005),
    (8882, 10005),
    (8881, 10006),
    (8882, 10006),
    (8881, 10007),
    (8882, 10007),
    (8881, 10008),
    (8882, 10008),
    (8881, 10009),
    (8882, 10009)
) AS user_event (user_id, event_id)
WHERE NOT EXISTS (SELECT 1
                  FROM user_event
                  WHERE user_id = user_event.user_id AND event_id = user_event.event_id);

-- Create 50 test user records with names in Ukrainian, English, and Ukrainian transliteration
INSERT INTO users (id, first_name, last_name, country, city, is_subscribed, picture, completed_interviews, conducted_interviews, email, password)
SELECT * FROM (
VALUES
    (8933, 'Андрій', 'Шевченко', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 8, 10, 'andrii.shevchenko1@example.com', 'password8933'),
    (8934, 'Andrew', 'Shevchenko', 'USA', 'New York', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 5, 7, 'andrew.shevchenko@example.com', 'password8934'),
    (8935, 'Andrii', 'Shevchenko', 'Ukraine', 'Lviv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 12, 9, 'andrii.shevchenko@example.com', 'password8935'),
    (8936, 'Марія', 'Іванова', 'Ukraine', 'Odessa', true, '', 10, 11, 'maria.ivanova1@example.com', 'password8936'),
    (8937, 'Maria', 'Ivanova', 'Canada', 'Toronto', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 14, 8, 'maria.ivanova@example.com', 'password8937'),
    (8938, 'Mariya', 'Ivanova', 'Ukraine', 'Dnipro', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 9, 7, 'mariya.ivanova@example.com', 'password8938'),
    (8939, 'Олексій', 'Коваленко', 'Ukraine', 'Kharkiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 7, 6, 'oleksii.kovalenko1@example.com', 'password8939'),
    (8940, 'Alex', 'Kovalenko', 'USA', 'Los Angeles', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 12, 'alex.kovalenko@example.com', 'password8940'),
    (8941, 'Oleksii', 'Kovalenko', 'Ukraine', 'Vinnytsia', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 6, 8, 'oleksii.kovalenko@example.com', 'password8941'),
    (8942, 'Іван', 'Петренко', 'Ukraine', 'Zaporizhzhia', true, '', 13, 10, 'ivan.petrenko1@example.com', 'password8942'),
    (8943, 'Ivan', 'Petrenko', 'USA', 'Chicago', true, '', 11, 9, 'ivan.petrenko@example.com', 'password8943'),
    (8944, 'Iwan', 'Petrenko', 'Ukraine', 'Poltava', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 8, 6, 'iwan.petrenko@example.com', 'password8944'),
    (8945, 'Софія', 'Сидоренко', 'Ukraine', 'Kyiv', true, '', 14, 13, 'sofia.sydorenko@example.com', 'password8945'),
    (8946, 'Sofia', 'Sydorenko', 'USA', 'San Francisco', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 9, 10, 'sofia.sydorenko1@example.com', 'password8946'),
    (8947, 'Sofiia', 'Sydorenko', 'Ukraine', 'Chernihiv', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 7, 'sofiia.sydorenko@example.com', 'password8947'),
    (8948, 'Дмитро', 'Мельник', 'Ukraine', 'Lutsk', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 11, 8, 'dmytro.melnyk1@example.com', 'password8948'),
    (8949, 'Dmytro', 'Melnyk', 'USA', 'Austin', false, '', 13, 9, 'dmytro.melnyk@example.com', 'password8949'),
    (8950, 'Dmitriy', 'Melnyk', 'Ukraine', 'Rivne', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 10, 'dmitriy.melnyk@example.com', 'password8950'),
    (8951, 'Анастасія', 'Гончаренко', 'Ukraine', 'Kherson', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 12, 11, 'anastasiia.honcharenko1@example.com', 'password8951'),
    (8952, 'Anastasia', 'Honcharenko', 'Canada', 'Montreal', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 14, 12, 'anastasia.honcharenko@example.com', 'password8952'),
    (8953, 'Anastasiia', 'Honcharenko', 'Ukraine', 'Ivano-Frankivsk', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 9, 8, 'anastasiia.honcharenko@example.com', 'password8953'),
    (8954, 'Олена', 'Бондар', 'Ukraine', 'Cherkasy', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 11, 'olena.bondar@example.com', 'password8954'),
    (8955, 'Olena', 'Bondar', 'USA', 'Boston', false, '', 8, 6, 'olena.bondar1@example.com', 'password8955'),
    (8956, 'Olha', 'Bondar', 'Ukraine', 'Sumy', true, '', 11, 9, 'olha.bondar@example.com', 'password8956'),
    (8957, 'Віктор', 'Захарчук', 'Ukraine', 'Ternopil', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 13, 8, 'viktor.zakharchuk@example.com', 'password8957'),
    (8958, 'Victor', 'Zakharchuk', 'USA', 'Seattle', false, '', 9, 10, 'victor.zakharchuk@example.com', 'password8958'),
    (8959, 'Viktor', 'Zakharchuk', 'Ukraine', 'Zhytomyr', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 15, 12, 'viktor.zakharchuk1@example.com', 'password8959'),
    (8960, 'Катерина', 'Романюк', 'Ukraine', 'Kyiv', true, '', 11, 8, 'katerina.romaniuk@example.com', 'password8960'),
    (8961, 'Katerina', 'Romaniuk', 'USA', 'Denver', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 7, 'katerina.romaniuk1@example.com', 'password8961'),
    (8962, 'Kateryna', 'Romaniuk', 'Ukraine', 'Lviv', true, '', 14, 9, 'kateryna.romaniuk@example.com', 'password8962'),
    (8963, 'Юлія', 'Савчук', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 12, 10, 'yuliya.savchuk1@example.com', 'password8963'),
    (8964, 'Julia', 'Savchuk', 'Canada', 'Vancouver', false, '', 13, 11, 'julia.savchuk@example.com', 'password8964'),
    (8965, 'Yuliya', 'Savchuk', 'Ukraine', 'Kharkiv', true, '', 8, 6, 'yuliya.savchuk@example.com', 'password8965'),
    (8966, 'Михайло', 'Олійник', 'Ukraine', 'Khmelnytskyi', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 9, 7, 'mykhailo.oliinyk1@example.com', 'password8966'),
    (8967, 'Michael', 'Oliinyk', 'USA', 'San Diego', false, '', 15, 14, 'michael.oliinyk@example.com', 'password8967'),
    (8968, 'Mykhailo', 'Oliinyk', 'Ukraine', 'Chernivtsi', true, '', 12, 11, 'mykhailo.oliinyk@example.com', 'password8968'),
    (8969, 'Ірина', 'Григоренко', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 11, 9, 'iryna.hryhorenko1@example.com', 'password8969'),
    (8970, 'Iryna', 'Hryhorenko', 'USA', 'New York', false, '', 10, 7, 'iryna.hryhorenko@example.com', 'password8970'),
    (8971, 'Irena', 'Hryhorenko', 'Ukraine', 'Mykolaiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 8, 6, 'irena.hryhorenko@example.com', 'password8971'),
    (8972, 'Тарас', 'Кравченко', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 14, 12, 'taras.kravchenko1@example.com', 'password8972'),
    (8973, 'Taras', 'Kravchenko', 'USA', 'Chicago', false, '', 11, 10, 'taras.kravchenko2@example.com', 'password8973'),
    (8974, 'Taras', 'Kravchenko', 'Ukraine', 'Kharkiv', true, '', 13, 11, 'taras.kravchenko@example.com', 'password8974'),
    (8975, 'Артем', 'Литвин', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 9, 'artem.lytvyn@example.com', 'password8975'),
    (8976, 'Artem', 'Lytvyn', 'USA', 'San Francisco', false, '', 12, 8, 'artem.lytvyn1@example.com', 'password8976'),
    (8977, 'Artemiy', 'Lytvyn', 'Ukraine', 'Dnipro', true, '', 14, 13, 'artemiy.lytvyn@example.com', 'password8977'),
    (8978, 'Наталія', 'Мороз', 'Ukraine', 'Kyiv', true, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 9, 7, 'natalia.moroz1@example.com', 'password8978'),
    (8979, 'Natalia', 'Moroz', 'USA', 'Miami', false, 'iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAIAAAADnC86AAAALUlEQVR4nO3NAQ0AAAgDILV/Z43xzUEBeitjQq9YLBaLxWKxWCwWLGFiVgkMAVzYG6LAAAAAElFTkSuQmCC', 10, 8, 'natalia.moroz@example.com', 'password8979'),
    (8980, 'Nataliya', 'Moroz', 'Ukraine', 'Poltava', true, '', 12, 10, 'nataliya.moroz@example.com', 'password8980')
) AS new_users (id, first_name, last_name, country, city, is_subscribed, picture, completed_interviews, conducted_interviews, email, password)
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE id = new_users.id);

-- Create test data specialization records with different names
INSERT INTO specializations (id, name, completed_interviews, conducted_interviews, is_main, user_id)
VALUES
    (6666, 'Python Developer', 5, 7, true, 8934),
    (6667, 'React Developer', 12, 9, true, 8935),
    (6668, 'UI/UX Designer', 10, 11, true, 8936),
    (6669, 'Full Stack Developer', 14, 8, true, 8937),
    (6670, 'Database Administrator', 9, 7, true, 8938),
    (6671, 'DevOps Engineer', 7, 6, true, 8939),
    (6673, 'System Analyst', 6, 8, true, 8941),
    (6674, 'Cloud Architect', 13, 10, true, 8942),
    (6675, 'Machine Learning Engineer', 11, 9, true, 8943),
    (6676, 'Scrum Master', 8, 6, true, 8944),
    (6677, 'iOS Developer', 14, 13, true, 8945),
    (6678, 'Frontend Developer', 9, 10, true, 8946),
    (6681, 'Backend Developer', 13, 9, true, 8949),
    (6682, 'Data Analyst', 10, 10, true, 8950),
    (6683, 'Product Designer', 12, 11, true, 8951),
    (6684, 'Software Engineer', 14, 12, true, 8952),
    (6686, 'Cloud Consultant', 8, 6, true, 8954),
    (6687, 'Project Manager', 9, 7, true, 8955),
    (6688, 'Data Engineer', 15, 14, true, 8956),
    (6691, 'React Native Developer', 13, 9, true, 8959),
    (6692, 'Network Engineer', 11, 9, true, 8960),
    (6693, 'AI Specialist', 10, 7, true, 8961),
    (6694, 'Cybersecurity Specialist', 14, 9, true, 8962),
    (6695, 'Quality Assurance', 8, 6, true, 8963),
    (6697, 'Frontend Consultant', 11, 8, true, 8965),
    (6699, 'Cloud Developer', 14, 10, true, 8967),
    (6700, 'AI Developer', 9, 8, true, 8968),
    (6701, 'Cloud Engineer', 12, 11, true, 8969),
    (6702, 'Data Scientist', 10, 7, true, 8970),
    (6704, 'Cloud Solutions Architect', 8, 6, true, 8972),
    (6705, 'Systems Administrator', 9, 10, true, 8973),
    (6706, 'Database Engineer', 14, 12, true, 8974),
    (6708, 'Frontend Developer', 10, 9, true, 8976),
    (6710, 'UX Researcher', 9, 7, true, 8978),
    (6711, 'Design Engineer', 13, 10, true, 8979),
    (6712, 'Product Designer', 12, 10, true, 8980)
ON CONFLICT (id) DO NOTHING;

-- Create test data for interview_feedback_details
INSERT INTO interview_feedback_details (id, participant_role, start_time, interview_summary_id, evaluated_mastery_id, skill_id, participant_id, owner_id)
SELECT * FROM (
VALUES
    (40001, 'INTERVIEWER', '2024-09-08T09:00:00Z'::timestamptz, 3001, 10001, ARRAY[100001, 100002, 100003, 100004, 100005]::bigint[], 8882, 8881),
    (40002, 'CANDIDATE', '2024-09-08T09:00:00Z'::timestamptz, 3001, 10001, ARRAY[100001, 100002, 100003, 100004, 100005, 100006, 100007, 100008, 100078, 100079, 100080]::bigint[], 8881, 8882)
) AS interview_feedback_details (id, participant_role, start_time, interview_summary_id, evaluated_mastery_id, skill_id, participant_id, owner_id)
WHERE NOT EXISTS (SELECT 1
                  FROM interview_feedback_details
                  WHERE id = interview_feedback_details.id);

-- Create test data for interview_summaries
INSERT INTO interview_summaries (id, date, duration, candidate_id, interviewer_id)
VALUES (3001, '2024-09-08', 60, 8881, 8882);

-- Create test data interview_summaries_users relation records
INSERT INTO interview_summaries_users (user_id, interview_summary_id)
SELECT * FROM (
VALUES
    (8881, 3001),
    (8882, 3001)
) AS interview_summaries_users (user_id, interview_summary_id)
WHERE NOT EXISTS (SELECT 1
                  FROM interview_summaries_users
                  WHERE user_id = interview_summaries_users.user_id
                    AND interview_summary_id = interview_summaries_users.interview_summary_id);
