--liquibase formatted sql
--changeset slair:2025_01_25-02-cms-create_inbox_direct_message_table

CREATE TABLE inbox_direct_message (
    inbox_id INTEGER REFERENCES profile(id),
    direct_message_id INTEGER REFERENCES direct_message(id),
    PRIMARY KEY(inbox_id, direct_message_id)
);