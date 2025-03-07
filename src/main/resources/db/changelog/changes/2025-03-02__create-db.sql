--liquibase formatted sql

--changeset AI:create-table-deleted-token
CREATE TABLE deleted_tokens (
    id VARCHAR(255) PRIMARY KEY
);