# Tasks hook

# --- !Ups

CREATE TABLE hook (
    id SERIAL PRIMARY KEY,
    url varchar(255) NOT NULL UNIQUE,
    tag_id int4 NOT NULL,
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

# --- !Downs

DROP TABLE IF EXISTS hook;
