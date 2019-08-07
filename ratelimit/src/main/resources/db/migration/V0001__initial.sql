CREATE TABLE earthquake (
    id            BIGINT NOT NULL AUTO_INCREMENT,
    time          TIMESTAMP NOT NULL,
    latitude      DECIMAL(10,7) NOT NULL,
    longitude     DECIMAL(10,7) NOT NULL,
    depth         DECIMAL(6,2) NOT NULL,
    mag           DECIMAL(4,2) NOT NULL,
    place         VARCHAR(255) NOT NULL,
    earthquake_id VARCHAR(18) NOT NULL,
    PRIMARY KEY(id)
);
