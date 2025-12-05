CREATE TABLE employee (
    id          SERIAL PRIMARY KEY,
    user_name   VARCHAR(100)    NOT NULL UNIQUE,
    birth_date  DATE            NOT NULL,
    first_name  VARCHAR(100)    NOT NULL,
    last_name   VARCHAR(100)    NOT NULL,
    gender      CHAR(1)         NOT NULL,
    hire_date   DATE            NOT NULL
);
