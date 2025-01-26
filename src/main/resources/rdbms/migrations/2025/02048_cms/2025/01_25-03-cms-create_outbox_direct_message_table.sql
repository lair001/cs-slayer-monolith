--liquibase formatted sql
--changeset slair:2025_01_25-03-cms-create_outbox_direct_message_table

CREATE TABLE outbox_direct_message (
    outbox_id INTEGER REFERENCES profile(id),
    direct_message_id INTEGER REFERENCES direct_message(id),
    PRIMARY KEY(outbox_id, direct_message_id)
);