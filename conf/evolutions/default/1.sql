# Tasks account and tag

# --- !Ups

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    email varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL
);

# --- !Downs

DROP TABLE IF EXISTS account;
