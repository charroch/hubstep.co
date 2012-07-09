# Tasks tag

# --- !Ups

CREATE TABLE tag (
    id SERIAL PRIMARY KEY,
    tagId varchar(255) NOT NULL UNIQUE,
    account_id int4 NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account(id)
);

# --- !Downs

DROP TABLE IF EXISTS tag;
