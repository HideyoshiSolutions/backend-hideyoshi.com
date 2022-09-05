CREATE SCHEMA IF NOT EXISTS auth;

CREATE SEQUENCE IF NOT EXISTS auth.user_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

CREATE TABLE IF NOT EXISTS auth.user (
    id          BIGINT          NOT NULL DEFAULT NEXTVAL('auth.user_seq'),
    full_name   VARCHAR(255)    NOT NULL,
    email       VARCHAR(255)    NOT NULL,
    username    VARCHAR(20)     NOT NULL,
    password      VARCHAR(100)     NOT NULL,
    roles       VARCHAR(50)     NOT NULL DEFAULT 'ROLE_USER',

    CONSTRAINT  client_primary_key      PRIMARY KEY     (id),
    CONSTRAINT  client_email_unique     UNIQUE          (email),
    CONSTRAINT  client_username_unique  UNIQUE          (username)
);