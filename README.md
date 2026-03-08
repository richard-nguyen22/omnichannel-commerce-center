# Omnichannel Commerce Center (Java 17)

Initial software architecture for omnichannel integrations (Lazada, Shopee, TikTok) with OAuth2 and Master Item module.

## Stack

- Java 17
- Spring Boot 3
- Maven

## IntelliJ setup

1. Open this folder in IntelliJ IDEA.
2. Let IntelliJ import Maven project from `pom.xml`.
3. Ensure Project SDK is Java 17.
4. Run `CommerceCenterApplication`.

## Current architecture

- `oauth`:
  - OAuth authorize URL generation and callback handling.
  - Provider strategy per channel: Lazada, Shopee, TikTok.
- `masteritem`:
  - Master item CRUD APIs as central catalog model.
- `channelaccount`:
  - Connected channel account and token storage.
- `common`:
  - Health endpoint and global exception handling.
- `config`:
  - Typed app/channel config (`application.yml`).

Persistence is currently in-memory repositories for bootstrapping.

## Endpoints

- `GET /health`
- `GET /oauth/{channel}/authorize?tenantId=tenant_1`
- `GET /oauth/{channel}/callback?code=...&state=...`
- `GET /channel-accounts?tenantId=tenant_1`
- `POST /master-items`
- `GET /master-items?tenantId=tenant_1`
- `GET /master-items/{id}`
- `PUT /master-items/{id}`

## Next steps

- Replace in-memory repositories with PostgreSQL (JPA/Flyway).
- Encrypt OAuth tokens at rest.
- Implement each channel's exact OAuth signature and token payload mapping.
- Add product sync, stock sync, and order ingestion jobs.
