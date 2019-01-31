CREATE TABLE employee (
    id          INTEGER NOT NULL AUTO_INCREMENT,
    user_name   VARCHAR(100)    NOT NULL,
    birth_date  DATE            NOT NULL,
    first_name  VARCHAR(100)    NOT NULL,
    last_name   VARCHAR(100)    NOT NULL,
    gender      CHAR(1)         NOT NULL,
    hire_date   DATE            NOT NULL,
    UNIQUE KEY (user_name),
    PRIMARY KEY(id)
);
