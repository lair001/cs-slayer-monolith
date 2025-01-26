--liquibase formatted sql
--changeset slair:2025_01_25-04-cms-create_blocked_profiles_table

CREATE TABLE blocked_profiles (
    blocker_id INTEGER REFERENCES profile(id),
    blockee_id INTEGER REFERENCES profile(id),
    PRIMARY KEY(blocker_id, blockee_id)
);