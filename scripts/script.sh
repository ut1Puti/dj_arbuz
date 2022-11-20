#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "postgres" --dbname "users_database" <<-EOSQL
        CREATE TABLE group_data ( group_name text NOT NULL, date_last_post bigint NOT NULL, users text NOT NULL, CONSTRAINT group_data_pkey PRIMARY KEY ( group_name ));
        CREATE TABLE user_data ( telegram_id text NOT NULL, user_id bigint NOT NULL, access_token text NOT NULL, CONSTRAINT user_data_pkey PRIMARY KEY ( telegram_id ));
EOSQL
