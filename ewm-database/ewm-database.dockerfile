FROM postgres:14-alpine
COPY 01-ewm-main-database-schema.sql /docker-entrypoint-initdb.d/