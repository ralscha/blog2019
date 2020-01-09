CREATE TABLE app_user (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    authority     VARCHAR(255),
    enabled       BOOLEAN NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(email)
);

CREATE TABLE app_session (
    id            CHAR(35)  NOT NULL,
    app_user_id   BIGINT    NOT NULL,
    valid_until   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id),
    FOREIGN KEY (app_user_id) REFERENCES app_user(id) ON DELETE CASCADE
);
