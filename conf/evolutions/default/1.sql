# Tasks account and tag

# --- !Ups

CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    email varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    username varchar(255) NOT NULL,
    fullname varchar(255),

    isAdmin BOOLEAN NOT NULL DEFAULT false,

    createdAt timestamp,
    modifiedAt timestamp
);

CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    uid varchar(255) NOT NULL UNIQUE,

    accountId INT NOT NULL,
    FOREIGN KEY (accountId) REFERENCES account(id),

    createdAt timestamp,
    modifiedAt timestamp
);

# --- !Downs

DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS tag;