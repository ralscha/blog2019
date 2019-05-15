CREATE TABLE app_user (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255),
    password_hash VARCHAR(255),
    authority     VARCHAR(255),
    enabled       BOOLEAN,
    PRIMARY KEY(id),
    UNIQUE(email)
);