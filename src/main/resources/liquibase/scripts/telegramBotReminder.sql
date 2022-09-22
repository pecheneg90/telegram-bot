-- liquibase formatted sql

-- changeset pecheneg:1

CREATE TABLE IF NOT EXISTS notification_task
(
    key INTEGER PRIMARY KEY ,
    idChat  INTEGER ,
    message text  ,
    dateTime TIMESTAMP
);