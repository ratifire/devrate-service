
-- Reserving 5 additional users (famous Ukrainian figures) for testing purposes E2E
INSERT INTO users (id, first_name, last_name, country, city, is_subscribed, description, completed_interviews, conducted_interviews)
VALUES
    (8990, 'Taras', 'Shevchenko', 'Ukraine', 'Kyiv', true, 'Famous poet and national hero of Ukraine.', 0, 0),
    (8991, 'Lesya', 'Ukrainka', 'Ukraine', 'Kyiv', true, 'Renowned Ukrainian writer and poet.', 0, 0),
    (8992, 'Ivan', 'Franko', 'Ukraine', 'Lviv', true, 'Prominent Ukrainian writer and social activist.', 0, 0),
    (8993, 'Bogdan', 'Khmelnitsky', 'Ukraine', 'Cherkasy', false, 'Leader of the Cossack uprising against Poland.', 0, 0),
    (8994, 'Mikhail', 'Grushevsky', 'Ukraine', 'Kyiv', true, 'Famous Ukrainian historian and statesman.', 0, 0);

-- Reserving corresponding records in the user_security table with hashed passwords for E2E tests
INSERT INTO user_security (id, email, is_verified, created_at, password, role_id, user_id)
VALUES
    (7780, 'dev.rate1+autotestuser1@proton.me', true, CURRENT_TIMESTAMP, '$2a$10$gpHUj9woEnFCGLNDy4MYVuCbmWapK.ZDgzqtZLZBvtIzKhh221pW6', 2, 8990),
    (7781, 'dev.rate1+autotestuser2@proton.me', true, CURRENT_TIMESTAMP, '$2a$10$5ynESBsukeVmwJM7LLbgo.RuIlIfT8T7CGSgj1roB3qUMSKuqJz7a', 2, 8991),
    (7782, 'dev.rate1+autotestuser3@proton.me', true, CURRENT_TIMESTAMP, '$2a$10$tqOZvKO2bRvHh69Jr/qVxOJgvUK5RM7kV9QNHK/CNREvbjeggkzlS', 2, 8992),
    (7783, 'dev.rate1+autotestuser4@proton.me', true, CURRENT_TIMESTAMP, '$2a$10$ZXV6PsoAI8W0stiBISJUBOkhyhE8QppjePCAcjzSofTZvbu.z2NXW', 2, 8993),
    (7784, 'dev.rate1+autotestuser5@proton.me', false, CURRENT_TIMESTAMP, '$2a$10$tXZCxVOzMwS/AQQctXlMburbtKaDIC3ap5P0lBpD65NVXBWjUIUMe', 2, 8994);

-- Reserving contact records for the test users E2E
INSERT INTO contacts (type, value, user_id)
VALUES
    ('EMAIL', 'dev.rate1+autotestuser1@proton.me', 8990),
    ('EMAIL', 'dev.rate1+autotestuser2@proton.me', 8991),
    ('EMAIL', 'dev.rate1+autotestuser3@proton.me', 8992),
    ('EMAIL', 'dev.rate1+autotestuser4@proton.me', 8993),
    ('EMAIL', 'dev.rate1+autotestuser5@proton.me', 8994);

-- Email: dev.rate1+autotestuser1@proton.me
-- Password: SecurePass1!
--
-- Email: dev.rate1+autotestuser2@proton.me
-- Password: SecurePass2!
--
-- Email: dev.rate1+autotestuser3@proton.me
-- Password: SecurePass3!
--
-- Email: dev.rate1+autotestuser4@proton.me
-- Password: SecurePass4!
--
-- Email: dev.rate1+autotestuser5@proton.me
-- Password: SecurePass5!
