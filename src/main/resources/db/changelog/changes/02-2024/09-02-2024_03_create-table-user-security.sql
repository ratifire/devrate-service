CREATE TABLE IF NOT EXISTS user_security
(
    id           BIGSERIAL PRIMARY KEY,
    password     VARCHAR(245)  NOT NULL,
    user_id      BIGINT UNIQUE NOT NULL,
    user_role_id BIGINT        NOT NULL

);
