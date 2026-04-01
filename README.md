# VIeaT PostgreSQL Migration Notes

This project previously used H2 in-memory databases inside each Spring Boot service. That was convenient for early development, but the data disappeared whenever a service restarted.

The project now uses PostgreSQL running in Docker. PostgreSQL is an external database server, so the services connect to it over the network. This is closer to how real microservice projects work, and the data stays available after container restarts because Docker stores it in a named volume.

## What Changed

- `customer-service`, `inventory-service`, and `order-service` now use the PostgreSQL JDBC driver instead of active H2 dependencies.
- Each service keeps its old H2 configuration as comments in `application.properties` so you can compare the before and after setup.
- Docker Compose now starts one PostgreSQL container and creates three databases:
  - `customerdb`
  - `inventorydb`
  - `orderdb`
- Seed SQL was made safer for a persistent database so restarts do not keep duplicating demo data.

## Why One PostgreSQL Container

One PostgreSQL container with multiple databases is the easiest beginner-friendly option:

- you only manage one database server
- each microservice still has its own database
- the Docker Compose file stays small and easier to understand

## How To Start Everything

From the project root:

```powershell
docker compose up --build
```

Useful commands:

```powershell
docker compose down
docker compose down -v
```

Use `docker compose down -v` only when you want to delete the PostgreSQL volume and start with a fresh database.

## How The Connection Works

When you run a Spring Boot service directly from your IDE, `localhost` in `application.properties` means your own computer, so the service connects to PostgreSQL through `localhost:5432`.

When you run the service inside Docker, `localhost` means the service container itself. That is why Docker Compose sets `DB_HOST=postgres`. On the Docker network, the service reaches the database container by its service name.

## Postman Testing

The REST APIs stay unchanged, so you can keep using the same Postman requests:

- customer-service: `http://localhost:8081`
- inventory-service: `http://localhost:8082`
- order-service: `http://localhost:8083`

Example flow:

1. Check customer endpoints on port `8081`
2. Check inventory endpoints on port `8082`
3. Create or query orders on port `8083`

## Connect To PostgreSQL Manually

If you have the PostgreSQL client installed on your machine:

```powershell
psql -h localhost -p 5432 -U food_user -d customerdb
psql -h localhost -p 5432 -U food_user -d inventorydb
psql -h localhost -p 5432 -U food_user -d orderdb
```

Password:

```text
food_password
```

You can also connect using IntelliJ Database Tools, DBeaver, or pgAdmin with:

- host: `localhost`
- port: `5432`
- username: `food_user`
- password: `food_password`
- database: one of `customerdb`, `inventorydb`, or `orderdb`
