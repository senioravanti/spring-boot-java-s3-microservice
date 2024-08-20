--liquibase formatted sql

--changeset senioravanti:update_schema
DROP TABLE IF EXISTS users;

--changeset senioravanti:create_users
--comment Создал таблицу users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    firstname VARCHAR(32),
    lastname VARCHAR(64),
  
    role VARCHAR(32) NOT NULL
);