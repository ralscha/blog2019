CREATE TABLE app_user (
	id              BIGINT NOT NULL AUTO_INCREMENT,
    username        VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255),
    secret          VARCHAR(16),
    enabled         BOOLEAN not null,
    PRIMARY KEY(id),
    UNIQUE(username)    
);
