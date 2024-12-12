-- Reserving additional users (famous Ukrainian figures + other famous figures) for testing purposes E2E
INSERT INTO users (id, first_name, last_name, country, city, is_subscribed, description, completed_interviews, conducted_interviews, email, password)
SELECT * FROM (
VALUES
    (8990, 'Taras', 'Shevchenko', 'Ukraine', 'Kyiv', true, 'Famous poet and national hero of Ukraine.', 0, 0, 'dev.rate1+autotestuser1@proton.me', '$2a$10$gpHUj9woEnFCGLNDy4MYVuCbmWapK.ZDgzqtZLZBvtIzKhh221pW6'),
    (8991, 'Lesya', 'Ukrainka', 'Ukraine', 'Kyiv', true, 'Renowned Ukrainian writer and poet.', 0, 0, 'dev.rate1+autotestuser2@proton.me', '$2a$10$5ynESBsukeVmwJM7LLbgo.RuIlIfT8T7CGSgj1roB3qUMSKuqJz7a'),
    (8992, 'Ivan', 'Franko', 'Ukraine', 'Lviv', true, 'Prominent Ukrainian writer and social activist.', 0, 0, 'dev.rate1+autotestuser3@proton.me', '$2a$10$tqOZvKO2bRvHh69Jr/qVxOJgvUK5RM7kV9QNHK/CNREvbjeggkzlS'),
    (8993, 'Bogdan', 'Khmelnitsky', 'Ukraine', 'Cherkasy', false, 'Leader of the Cossack uprising against Poland.', 0, 0, 'dev.rate1+autotestuser4@proton.me', '$2a$10$ZXV6PsoAI8W0stiBISJUBOkhyhE8QppjePCAcjzSofTZvbu.z2NXW'),
    (8994, 'Mikhail', 'Grushevsky', 'Ukraine', 'Kyiv', true, 'Famous Ukrainian historian and statesman.', 0, 0, 'dev.rate1+autotestuser5@proton.me', '$2a$10$tXZCxVOzMwS/AQQctXlMburbtKaDIC3ap5P0lBpD65NVXBWjUIUMe'),
    (8995, 'Johnny', 'Depp', 'USA', 'Los Angeles', true, 'Famous American actor known for diverse roles.', 0, 0, 'dev.rate2+manualtestuser1@proton.me', '$2a$10$ghif4NlGn7PwHzh/oSZGRuFrB3DgQHtaEVaJU9OgrU6OEzCiwUJeK'),
    (8996, 'Scarlett', 'Johansson', 'USA', 'New York', true, 'Famous American actress known for her action and dramatic roles.', 0, 0, 'dev.rate2+manualtestuser2@proton.me', '$2a$10$bzkxLFj6H7vhgGCkvX5BuOiZ5X3OR.Jjr8v17PrUJlWQ6g7cUBlUS'),
    (8997, 'Keanu', 'Reeves', 'USA', 'Los Angeles', true, 'Famous actor known for The Matrix and John Wick series.', 0, 0, 'dev.rate2+manualtestuser3@proton.me', '$2a$10$3c2rqpAKkO0tY7DZrV9QBem0wp/hRvB25GoXJFVxt8l4mcQLt/MdO'),
    (8998, 'Angelina', 'Jolie', 'USA', 'Los Angeles', true, 'Acclaimed actress and humanitarian.', 0, 0, 'dev.rate2+manualtestuser4@proton.me', '$2a$10$vE/Mv6I.R.HRoCFCDW9ScJeDsblr0FhL8v35tL3gOfeWyTmC28lvXy'),
    (8999, 'Elon', 'Musk', 'USA', 'Austin', true, 'Entrepreneur, founder of SpaceX and Tesla.', 0, 0, 'dev.rate2+manualtestuser5@proton.me', '$2a$10$bER8SJXhruruAt3AldO6tuTEuyv4WhmJgC7Ihld3F1qy1SH5gV/Ja'),
    (9000, 'Bill', 'Gates', 'USA', 'Seattle', true, 'Co-founder of Microsoft and philanthropist.', 0, 0, 'dev.rate2+manualtestuser6@proton.me', '$2a$10$vMU7Rf4tp2m5.VnX/Iy9eelLG5NUVRXq11fNTmlCS91LeAH7SBMB.'),
    (9001, 'Mark', 'Zuckerberg', 'USA', 'Palo Alto', true, 'Co-founder and CEO of Facebook.', 0, 0, 'dev.rate2+manualtestuser7@proton.me', '$2a$10$tim6qkk3DnFBvCW8R4GS6OFVwqEHEMJZdzINH5gpglYrN84NW/XgO'),
    (9002, 'Steve', 'Jobs', 'USA', 'San Francisco', true, 'Co-founder of Apple, pioneer of the personal computer.', 0, 0, 'dev.rate2+manualtestuser8@proton.me', '$2a$10$x6Ge0eY/7TyrUe5ZUzRTvejN1Gz9lgIhBt1zUHt2oBmW9Kb6dMl8u'),
    (9003, 'Oprah', 'Winfrey', 'USA', 'Chicago', true, 'Media mogul and philanthropist.', 0, 0, 'dev.rate2+manualtestuser9@proton.me', '$2a$10$.6uQ4C3uPG6ro8DZn0WUVe2gjS4VVF8FF7445xEYsSeJKDjXmdKPK'),
    (9004, 'JK', 'Rowling', 'UK', 'Edinburgh', true, 'Author of the Harry Potter series.', 0, 0, 'dev.rate2+manualtestuser10@proton.me', '$2a$10$bDH4yyWcoqb28ibI2TXTWumujxDqnlyjT8GFeb11n3uEvz4rwfEQ6'),
    (9005, 'Barack', 'Obama', 'USA', 'Washington, D.C.', true, '44th President of the United States.', 0, 0, 'dev.rate2+manualtestuser11@proton.me', '$2a$10$kk.IBgO6tZhVBugE/Dj6/.XaH/.gQRC68ZZ65eTwLh/Iqg/k09AXa'),
    (9006, 'Cristiano', 'Ronaldo', 'Portugal', 'Lisbon', true, 'World-renowned football player.', 0, 0, 'dev.rate2+manualtestuser12@proton.me', '$2a$10$Q6.Nm2rJ1PnQLEJhQ/iL4esglSCp4xRBc2mVFOQ65oeVf1yrDXxAm'),
    (9007, 'Lionel', 'Messi', 'Argentina', 'Rosario', true, 'Legendary football player.', 0, 0, 'dev.rate2+manualtestuser13@proton.me', '$2a$10$XXPJbAyqaOHuGVNS6euzn.FMNy1NjrHXukquXE0/gXxVc2LuLTTQK'),
    (9008, 'Usain', 'Bolt', 'Jamaica', 'Kingston', true, 'Olympic gold medalist and fastest man in history.', 0, 0, 'dev.rate2+manualtestuser14@proton.me', '$2a$10$cBb1sfNfOSzV791hGUa3D.4EVIScxjQzI9hhNINpG4uZsM0dzz2Z6'),
    (9009, 'Malala', 'Yousafzai', 'Pakistan', 'Mingora', true, 'Youngest Nobel Prize laureate.', 0, 0, 'dev.rate2+manualtestuser15@proton.me', '$2a$10$/iUwBd573GxeuFJuynjKDeVews539eDNvaknYgakUnJU4SDezXuVm'),
    (9010, 'Leonardo', 'da Vinci', 'Italy', 'Florence', true, 'Renaissance polymath and artist.', 0, 0, 'dev.rate2+manualtestuser16@proton.me', '$2a$10$v5axi.fdn5W7yATQekOY5.g5FYn1yQVxaXZMcIe2FCr0JwfFc.7Ny'),
    (9011, 'Galileo', 'Galilei', 'Italy', 'Pisa', true, 'Astronomer, physicist, and engineer.', 0, 0, 'dev.rate2+manualtestuser17@proton.me', '$2a$10$IeVZ0bZmAW57TQlp.AnZyu7HWgICaz/H0lvU.ABNKhBmppCwgNdo.'),
    (9012, 'Nelson', 'Mandela', 'South Africa', 'Johannesburg', true, 'Anti-apartheid revolutionary and President.', 0, 0, 'dev.rate2+manualtestuser18@proton.me', '$2a$10$IlH20K9AqR8KX2.sDPBtF.zQP99SvZxIg8P10nuIZ5dLWRjxPwQwi'),
    (9013, 'Albert', 'Einstein', 'Germany', 'Ulm', true, 'Theoretical physicist who developed the theory of relativity.', 0, 0, 'dev.rate2+manualtestuser19@proton.me', '$2a$10$Ec2kKdIPbXaqlM3nDub3wuKqW5CBFDjxcDFA1086WLk8g2BhBTSt2'),
    (9014, 'Marie', 'Curie', 'France', 'Paris', true, 'Pioneer in radioactivity and first woman to win a Nobel Prize.', 0, 0, 'dev.rate2+manualtestuser20@proton.me', '$2a$10$szgsj6onRApANT69yMQYZ.RjmivMjoNaHaijSvJOPrwnNolIyVgTq'),
    (9015, 'Isaac', 'Newton', 'UK', 'Woolsthorpe', true, 'Mathematician and physicist who formulated the laws of motion.', 0, 0, 'dev.rate2+manualtestuser21@proton.me', '$2a$10$KK7r4MK3F00wP2HxrvphTOfAK/tTxDVeNRFZoOjRS5sP0DmFDtVWe'),
    (9016, 'Vincent', 'van Gogh', 'Netherlands', 'Zundert', true, 'Post-impressionist painter famous for Starry Night.', 0, 0, 'dev.rate2+manualtestuser22@proton.me', '$2a$10$D/c41be.FKddIBQ7qbijk.crI2fM.26cotHPiVpINl85zD.tqn7Y6')
) AS new_users (id, first_name, last_name, country, city, is_subscribed, picture, completed_interviews, conducted_interviews, email, password)
WHERE NOT EXISTS (SELECT 1
                  FROM users
                  WHERE id = new_users.id);

-- Reserving contact records for the test users E2E
INSERT INTO contacts (type, value, user_id)
SELECT type, value, user_id
FROM (
VALUES
    ('EMAIL', 'dev.rate1+autotestuser1@proton.me', 8990),
    ('EMAIL', 'dev.rate1+autotestuser2@proton.me', 8991),
    ('EMAIL', 'dev.rate1+autotestuser3@proton.me', 8992),
    ('EMAIL', 'dev.rate1+autotestuser4@proton.me', 8993),
    ('EMAIL', 'dev.rate1+autotestuser5@proton.me', 8994),
    ('EMAIL', 'dev.rate2+manualtestuser1@proton.me', 8995),
    ('EMAIL', 'dev.rate2+manualtestuser2@proton.me', 8996),
    ('EMAIL', 'dev.rate2+manualtestuser3@proton.me', 8997),
    ('EMAIL', 'dev.rate2+manualtestuser4@proton.me', 8998),
    ('EMAIL', 'dev.rate2+manualtestuser5@proton.me', 8999),
    ('EMAIL', 'dev.rate2+manualtestuser6@proton.me', 9000),
    ('EMAIL', 'dev.rate2+manualtestuser7@proton.me', 9001),
    ('EMAIL', 'dev.rate2+manualtestuser8@proton.me', 9002),
    ('EMAIL', 'dev.rate2+manualtestuser9@proton.me', 9003),
    ('EMAIL', 'dev.rate2+manualtestuser10@proton.me', 9004),
    ('EMAIL', 'dev.rate2+manualtestuser11@proton.me', 9005),
    ('EMAIL', 'dev.rate2+manualtestuser12@proton.me', 9006),
    ('EMAIL', 'dev.rate2+manualtestuser13@proton.me', 9007),
    ('EMAIL', 'dev.rate2+manualtestuser14@proton.me', 9008),
    ('EMAIL', 'dev.rate2+manualtestuser15@proton.me', 9009),
    ('EMAIL', 'dev.rate2+manualtestuser16@proton.me', 9010),
    ('EMAIL', 'dev.rate2+manualtestuser17@proton.me', 9011),
    ('EMAIL', 'dev.rate2+manualtestuser18@proton.me', 9012),
    ('EMAIL', 'dev.rate2+manualtestuser19@proton.me', 9013),
    ('EMAIL', 'dev.rate2+manualtestuser20@proton.me', 9014),
    ('EMAIL', 'dev.rate2+manualtestuser21@proton.me', 9015),
    ('EMAIL', 'dev.rate2+manualtestuser22@proton.me', 9016)
     ) AS new_contacts (type, value, user_id)
WHERE NOT EXISTS (
    SELECT 1
    FROM contacts
    WHERE contacts.user_id = new_contacts.user_id
);


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
--
-- Email: dev.rate2+manualtestuser1@proton.me
-- Password: Pirate1234@!
--
-- Email: dev.rate2+manualtestuser2@proton.me
-- Password: BlackWidow1@!
--
-- Email: dev.rate2+manualtestuser3@proton.me
-- Password: NeoMatrix123@!
--
-- Email: dev.rate2+manualtestuser4@proton.me
-- Password: TombRaider123@!
--
-- Email: dev.rate2+manualtestuser5@proton.me
-- Password: SpaceX123@!
--
-- Email: dev.rate2+manualtestuser6@proton.me
-- Password: Microsoft123@!
--
-- Email: dev.rate2+manualtestuser7@proton.me
-- Password: Facebook123@!
--
-- Email: dev.rate2+manualtestuser8@proton.me
-- Password: Apple123@!
--
-- Email: dev.rate2+manualtestuser9@proton.me
-- Password: TalkShow123@!
--
-- Email: dev.rate2+manualtestuser10@proton.me
-- Password: HarryPotter123@!
--
-- Email: dev.rate2+manualtestuser11@proton.me
-- Password: President123@!
--
-- Email: dev.rate2+manualtestuser12@proton.me
-- Password: Football123@!
--
-- Email: dev.rate2+manualtestuser13@proton.me
-- Password: Soccer123@!
--
-- Email: dev.rate2+manualtestuser14@proton.me
-- Password: FastestMan123@!
--
-- Email: dev.rate2+manualtestuser15@proton.me
-- Password: Education123@!
--
-- Email: dev.rate2+manualtestuser16@proton.me
-- Password: MonaLisa123@!
--
-- Email: dev.rate2+manualtestuser17@proton.me
-- Password: Astronomy123@!
--
-- Email: dev.rate2+manualtestuser18@proton.me
-- Password: Freedom123@!
--
-- Email: dev.rate2+manualtestuser19@proton.me
-- Password: Relativity123@!
--
-- Email: dev.rate2+manualtestuser20@proton.me
-- Password: Radioactive123@!
--
-- Email: dev.rate2+manualtestuser21@proton.me
-- Password: Gravity123@!
--
-- Email: dev.rate2+manualtestuser22@proton.me
-- Password: StarryNight123@!