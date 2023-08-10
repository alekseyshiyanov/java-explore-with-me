FROM postgres:14-alpine
COPY 01-schema.sql /docker-entrypoint-initdb.d/