
-- Reserving 5 additional users (famous Ukrainian figures) for testing purposes E2E
INSERT INTO users (id, first_name, last_name, country, city, is_subscribed, description, completed_interviews, conducted_interviews)
VALUES
    (8990, 'Taras', 'Shevchenko', 'Ukraine', 'Kyiv', false, 'Famous poet and national hero of Ukraine.', 0, 0),
    (8991, 'Lesya', 'Ukrainka', 'Ukraine', 'Kyiv', false, 'Renowned Ukrainian writer and poet.', 0, 0),
    (8992, 'Ivan', 'Franko', 'Ukraine', 'Lviv', false, 'Prominent Ukrainian writer and social activist.', 0, 0),
    (8993, 'Bogdan', 'Khmelnitsky', 'Ukraine', 'Cherkasy', false, 'Leader of the Cossack uprising against Poland.', 0, 0),
    (8994, 'Mikhail', 'Grushevsky', 'Ukraine', 'Kyiv', false, 'Famous Ukrainian historian and statesman.', 0, 0);

-- Reserving corresponding records in the user_security table with hashed passwords for E2E tests
INSERT INTO user_security (id, email, is_verified, created_at, password, role_id, user_id)
VALUES
    (7780, 'dev.rate1+autotestuser1@proton.me', false, CURRENT_TIMESTAMP, '$2a$12$KXChZxqQjlmLK72NRlL3y./VJgRYoa6X5x.BY7SMsNoj/VGV0ZxJ2', 2, 8990),
    (7781, 'dev.rate1+autotestuser2@proton.me', false, CURRENT_TIMESTAMP, '$2a$12$EBzLRJlkGoZhkV68BjRu2uzL/yVup2bF48sT3XumrEKB4Qb5OyeC.', 2, 8991),
    (7782, 'dev.rate1+autotestuser3@proton.me', false, CURRENT_TIMESTAMP, '$2a$12$JlQdgRTzD1yb7RKn3fzqzOZQpGxeH8PVnTkIAAvRAOq.cBovw7ZZK', 2, 8992),
    (7783, 'dev.rate1+autotestuser4@proton.me', false, CURRENT_TIMESTAMP, '$2a$12$uOrpVi9u.Gg5Ex41/MIceOb.s87FUwRGmfskXjOvZV2oy/IbsxI3i', 2, 8993),
    (7784, 'dev.rate1+autotestuser5@proton.me', false, CURRENT_TIMESTAMP, '$2a$12$SjZj83XjFbnN09TfS2qM7eX12VcmQZOTmWeJMI/KSCscMPGjQ67aO', 2, 8994);

-- Reserving contact records for the test users E2E
INSERT INTO contacts (type, value, user_id)
VALUES
    ('EMAIL', 'dev.rate1+autotestuser1@proton.me', 8990),
    ('EMAIL', 'dev.rate1+autotestuser2@proton.me', 8991),
    ('EMAIL', 'dev.rate1+autotestuser3@proton.me', 8992),
    ('EMAIL', 'dev.rate1+autotestuser4@proton.me', 8993),
    ('EMAIL', 'dev.rate1+autotestuser5@proton.me', 8994);
