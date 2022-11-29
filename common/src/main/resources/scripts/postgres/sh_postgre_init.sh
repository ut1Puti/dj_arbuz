#!/bin/sh
set -e
psql -v ON_ERROR_STOP=1 --username "postgres" --dbname "users_database" <<-EOSQL
        CREATE TABLE group_data (
            group_name text NOT NULL,
            date_last_post bigint NOT NULL,
            CONSTRAINT group_data_pkey PRIMARY KEY (group_name)
        );
        CREATE TABLE user_data (
            telegram_id text NOT NULL,
            user_id bigint NOT NULL,
            access_token text NOT NULL,
            CONSTRAINT user_data_pkey PRIMARY KEY (telegram_id)
        );
        CREATE TABLE subscribers (
            user_telegram_id text NOT NULL,
            group_name text NOT NULL,
            CONSTRAINT subscribers_pkey PRIMARY KEY (user_telegram_id, group_name),
            CONSTRAINT user_fkey FOREIGN KEY (user_telegram_id) REFERENCES user_data (telegram_id),
            CONSTRAINT group_fkey FOREIGN KEY (group_name) REFERENCES group_data (group_name)
        )
EOSQL
