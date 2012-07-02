# Tasks profile and tag

# --- !Ups

CREATE TABLE profile (
    id SERIAL PRIMARY KEY,
    email varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,

    googleID varchar(255),
    verifiedEmail BOOLEAN,
    name varchar(255),
    givenName varchar(255),
    familyName varchar(255),
    link varchar(255),
    picture varchar(255),
    gender varchar(255),
    locale varchar(255),

    createdAt timestamp,
    modifiedAt timestamp
);

CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    uid varchar(255) NOT NULL UNIQUE,

    profileId INT NOT NULL,
    FOREIGN KEY (profileId) REFERENCES profile(id),

    createdAt timestamp,
    modifiedAt timestamp
);

# --- !Downs

DROP TABLE IF EXISTS profile;
DROP TABLE IF EXISTS google_profile;
DROP TABLE IF EXISTS tag;