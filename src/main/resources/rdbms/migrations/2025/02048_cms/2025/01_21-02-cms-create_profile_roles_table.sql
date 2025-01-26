--liquibase formatted sql
--changeset slair:2025_01_21-02-cms-create_profile_roles_table

CREATE TABLE profile_roles (
    profile_id INTEGER REFERENCES profile(id),
    role_id SMALLINT REFERENCES role(id),
    PRIMARY KEY(profile_id, role_id)
);
