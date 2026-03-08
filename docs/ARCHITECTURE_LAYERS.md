# Architecture Layers Guide

This project follows layered architecture to keep responsibilities clear and prevent business logic from leaking into controllers or SQL code.

## Layer Responsibilities

### `deployment`
- Purpose: expose external API endpoints (HTTP controllers).
- Should do:
  - Parse path/query/body input.
  - Call application service.
  - Return response object.
- Should not do:
  - SQL queries.
  - Liquibase or driver execution.
  - Business workflow logic.

Current example:
- `LiquibaseDeploymentController` handles `/run`, `/status`, `/clear-checksum` endpoints.

### `application`
- Purpose: orchestrate use-cases and business flow.
- Should do:
  - Sequence operations across repository/integration.
  - Apply use-case level decisions (e.g. run all ACTIVE tenants).
  - Translate missing resources to domain/application errors.
- Should not do:
  - Low-level JDBC mapping.
  - Framework-specific controller concerns.

Current example:
- `LiquibaseDeploymentApplicationService` orchestrates tenant selection + liquibase actions.

### `repository`
- Purpose: read/write data in core system database.
- Should do:
  - SQL and row mapping.
  - Return typed model classes.
- Should not do:
  - HTTP handling.
  - Orchestrating multiple workflows.

Current examples:
- `ClientTenantHeaderRepository`.
- `UserLoginRepository`.

### `integration`
- Purpose: connect to external systems or external resources.
- Should do:
  - Third-party calls.
  - Cross-database execution (tenant DB liquibase).
  - Convert external response/result into typed integration result.
- Should not do:
  - Endpoint parsing.
  - Business orchestration for full use-case.

Current example:
- `TenantLiquibaseIntegration`.

### `domain/model` (`user`, `auth`, etc.)
- Purpose: represent business data structures and local domain rules.
- Should do:
  - POJO/entity structures.
  - Enum constraints.
  - Pure validation/policy helpers.
- Should not do:
  - JDBC operations.
  - Controller logic.

Current examples:
- `UserLogin`, `UserRegistration`, `UserStatus`.

## Why create a "container" for `user_login`?

In Spring terms, a "container" usually means a managed bean (`@Repository`, `@Service`, `@Component`).

For `user_login`, you need two different classes:
- Data model class: `UserLogin` (already created) to represent one row.
- Data access container: `UserLoginRepository` (added) to load/save rows using JDBC.

Without a repository container, your SQL would leak into controllers/services and break layering. So yes, creating a repository container for `user_login` is needed.

## Quick Rule

- Need only shape of data? create a model class.
- Need DB operations? create a repository container.
- Need use-case orchestration? create an application service container.
- Need HTTP endpoint? create a deployment controller.

## Current Package Map

- `com.omnichannel.center.deployment.*`: REST controllers and request models.
- `com.omnichannel.center.application.*`: use-case orchestration and business services.
- `com.omnichannel.center.repository.*`: SQL/JDBC and persistence mapping.
- `com.omnichannel.center.integration.*`: external integrations (Liquibase per-tenant execution, OAuth providers).
- `com.omnichannel.center.domain.*`: domain entities/enums/pure policies.
- `com.omnichannel.center.config`: typed app configuration.
- `com.omnichannel.center.common`: shared cross-cutting concerns (exceptions, handlers).
