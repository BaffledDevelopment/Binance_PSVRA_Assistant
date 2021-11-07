DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'dvornik')
        THEN CREATE ROLE "dvornik" NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN PASSWORD 'password';
    END IF;
END$$;

DO
$do$
BEGIN
  IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
        WHERE rolname = 'fill_klines')
    THEN
        CREATE ROLE "fill_klines" NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN PASSWORD 'password';
    END IF;
END
$do$;

DO
$do$
BEGIN
  IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
        WHERE rolname = 'klines_reader')
    THEN
        CREATE ROLE "klines_reader" NOSUPERUSER NOCREATEDB NOCREATEROLE NOINHERIT LOGIN PASSWORD 'password';
    END IF;
END
$do$;