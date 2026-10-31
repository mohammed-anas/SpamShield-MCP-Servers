# Spam Shield MCP Servers

Reactive servers for Spam Shield. Each one is its own Spring Boot process and its own container. They use Postgres. They do not call a chat model.

The MCP host and the model run outside this repository. Clients that insert, update, or delete call records call `call-records-rest-server` directly. The MCP host calls `call-records-mcp-server` over MCP. Both use the `CallRecord` row in `spam-shield-common`.

## Layout

```
.
├── pom.xml
├── docker-compose.yml
├── docker/Dockerfile
├── spam-shield-common              shared config, CallRecord, and SQL
├── call-records-mcp-server         MCP, port 9090
└── call-records-rest-server        HTTP CRUD, port 8081
```

`spam-shield-common` is a library. It is not deployed. Its `spamshield-shared.yaml` is imported by each server with `spring.config.import: classpath:spamshield-shared.yaml`.

`call-records-mcp-server` speaks MCP over Streamable HTTP at `POST /mcp`. `call-records-rest-server` is the HTTP API for call records. Health for both is `/actuator/health`.

| Method | Path | Effect |
| --- | --- | --- |
| `POST` | `/api/call-records` | Insert a call record |
| `GET` | `/api/call-records/{id}` | Read one active record |
| `GET` | `/api/call-records` | List active records. Optional `callerNumber`, `limit`, `offset` |
| `PUT` | `/api/call-records/{id}` | Replace the fields of an active record |
| `DELETE` | `/api/call-records/{id}` | Soft-delete. Later reads skip the row |

## Prerequisites

- JDK 25
- Docker with Compose v2, when you want the server image
- Postgres already running
- Maven wrapper (`./mvnw`)

## Start on the host

Defaults match a local Postgres database named `spamshield`, user `spamshield`, password `spamshield`.

```bash
cp .env.example .env
export $(grep -v '^#' .env | xargs)
```

Apply the shared schema once, in that database:

```bash
psql -h localhost -U spamshield -d spamshield -f spam-shield-common/src/main/resources/db/schema.sql
```

Run one server:

```bash
./mvnw -pl call-records-mcp-server -am spring-boot:run
./mvnw -pl call-records-rest-server -am spring-boot:run
```

| Process | Endpoint |
| --- | --- |
| Call records MCP | http://localhost:9090/mcp |
| Call records REST | http://localhost:8081/api/call-records |

```bash
curl -s http://localhost:8081/actuator/health
```

Create a row once Postgres is up and `call_record` exists:

```bash
curl -s -X POST http://localhost:8081/api/call-records \
  -H 'content-type: application/json' \
  -d '{"callerNumber":"+15551212","calleeNumber":"+15557676","direction":"INBOUND","startedAt":"2026-10-31T12:00:00Z","durationSeconds":42}'
```

`./mvnw test` does not need a running database. The pool waits until a tool queries.

Override the database without editing YAML:

```bash
export SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5432/spamshield
export SPRING_R2DBC_USERNAME=spamshield
export SPRING_R2DBC_PASSWORD=spamshield
```

## Run in Docker

Postgres must already be reachable at `host.docker.internal:5432`. These commands do not start the database or a model. Each container listens on the same port as on the host: 9090 for MCP and 8081 for the REST API.

```bash
docker compose up --build -d
```

Stop and remove it:

```bash
docker compose down
```

The same service on its own:

```bash
docker compose up --build -d call-records-mcp-server
docker compose up --build -d call-records-rest-server
docker compose rm -sf call-records-rest-server
```

`docker compose down` stops every service in this file. `docker compose rm -sf` stops and removes the named service. `-s` stops it first, and `-f` skips the confirm prompt.

Build and run that image without Compose:

```bash
docker build -f docker/Dockerfile --target call-records-mcp-server -t spamshield/call-records-mcp-server .
docker run --rm -d \
  --name call-records-mcp-server \
  -p 9090:9090 \
  --add-host host.docker.internal:host-gateway \
  -e SERVER_PORT=9090 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://host.docker.internal:5432/spamshield \
  -e SPRING_R2DBC_USERNAME=spamshield \
  -e SPRING_R2DBC_PASSWORD=spamshield \
  spamshield/call-records-mcp-server
```

`--rm` removes the container when it stops:

```bash
docker stop call-records-mcp-server
```

The REST image uses the same shape with `--target call-records-rest-server`, image `spamshield/call-records-rest-server`, `-e SERVER_PORT=8081`, and `-p 8081:8081`.

Username and password default to `spamshield` when those variables are unset. Compose reads `SPRING_R2DBC_USERNAME` and `SPRING_R2DBC_PASSWORD` from the shell or from a `.env` file next to `docker-compose.yml`.

## Add another MCP server

1. Copy `call-records-mcp-server` and rename the artifact, package, and tools.
2. Add the module to the parent `pom.xml`.
3. Add a `FROM runtime AS <module>` stage in `docker/Dockerfile`.
4. Add a service in `docker-compose.yml` with its own host port.
5. Add any new table to `spam-shield-common/src/main/resources/db/schema.sql` and apply it to the external database.

Keep `spring-boot-starter-web` off the classpath. These servers are WebFlux.

## Stack

- Java 25, Spring Boot 4.1.2, Spring WebFlux
- Spring AI 2.0.2 MCP server starter (`spring-ai-starter-mcp-server-webflux`)
- Spring Data R2DBC and `r2dbc-postgresql`
- Maven wrapper
