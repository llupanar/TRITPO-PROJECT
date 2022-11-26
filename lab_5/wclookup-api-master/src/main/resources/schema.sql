CREATE TABLE toilet (
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(256) NOT NULL,
    schedule VARCHAR(256) NOT NULL,
    latitude DECIMAL NOT NULL,
    longitude DECIMAL NOT NULL,
    rating DECIMAL(4, 3) NOT NULL,
    confirmed BOOLEAN NOT NULL
);

CREATE TABLE ticket (
    id BIGSERIAL PRIMARY KEY,
    subject VARCHAR(140) NOT NULL,
    text VARCHAR(1024) NOT NULL,
    email VARCHAR(100) NOT NULL,
    creation_time TIMESTAMP NOT NULL,
    resolved BOOLEAN NOT NULL
);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    google_id VARCHAR(256) NOT NULL UNIQUE,
    nickname VARCHAR(32) NOT NULL,
    refresh_token CHAR(64),
    refresh_token_expiration TIMESTAMP,
    is_admin BOOLEAN NOT NULL
);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    toilet_id BIGINT NOT NULL,
    rating DECIMAL(4, 3) NOT NULL,
    text VARCHAR(140),
    creation_time TIMESTAMP NOT NULL,

    CONSTRAINT review_to_user_fk FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT review_to_toilet_fk FOREIGN KEY (toilet_id) REFERENCES toilet (id) ON DELETE CASCADE
);

CREATE FUNCTION getDistance(lat1 DOUBLE PRECISION, long1 DOUBLE PRECISION, lat2 DOUBLE PRECISION, long2 DOUBLE PRECISION)
    RETURNS DOUBLE PRECISION
    LANGUAGE plpgsql
AS $$
DECLARE
    x float = 69.1 * (lat2 - lat1);
    y float = 69.1 * (long2 - long1) * cos(lat1 / 57.3);
BEGIN
RETURN sqrt(x * x + y * y);
END$$;

CREATE OR REPLACE FUNCTION insertUpdateHandler()
    RETURNS trigger
    LANGUAGE plpgsql
AS $$
BEGIN
UPDATE toilet
SET rating = COALESCE((SELECT AVG(rating) FROM review WHERE toilet_id = new.toilet_id), 0)
WHERE id = new.toilet_id;
RETURN new;
END $$;

CREATE OR REPLACE FUNCTION deleteHandler()
    RETURNS trigger
    LANGUAGE plpgsql
AS $$
BEGIN
UPDATE toilet
SET rating = COALESCE((SELECT AVG(rating) FROM review WHERE toilet_id = old.toilet_id), 0)
WHERE id = old.toilet_id;
RETURN new;
END $$;

CREATE TRIGGER on_review_insert_update
    AFTER INSERT OR UPDATE ON review
                        FOR EACH ROW
                        EXECUTE PROCEDURE insertUpdateHandler();

CREATE TRIGGER on_review_delete
    AFTER DELETE ON review
    FOR EACH ROW
    EXECUTE PROCEDURE deleteHandler();
