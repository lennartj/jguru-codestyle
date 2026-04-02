# ParBricole Services Claude Instructions

## JetBrains MCP Tools — Prefer Over Bash for Navigation and Code Intelligence

The JetBrains IntelliJ IDEA MCP server is available and provides IDE-indexed tools that are faster and more accurate
than shell equivalents for **navigation, search, and code intelligence tasks**. Use these tools by default for those
tasks, and avoid additional shell calls where possible. Do not add any new MCP tools to this list if they send the
corporate IP (i.e. source code) outside the company.

**Note:** For build, test, and project structure operations, use Maven by default (see section below).

| Task | Use (JetBrains MCP) | Instead of | Key params |
|------|---------------------|------------|------------|
| Browse directory | `mcp__idea__list_directory_tree` | `find`, `ls -R` | `maxDepth=2` (depth 1 = root only) |
| Create file | `mcp__idea__create_new_file` | `Write`, `touch` | `overwrite=false` (safe default) |
| Diagnostics (errors/warnings) | `mcp__idea__get_file_problems` | n/a | `errorsOnly=true` to reduce noise |
| Edit file | `mcp__idea__replace_text_in_file` | `Edit`, `sed` | `replaceAll=false` for targeted edits |
| Find files by glob | `mcp__idea__search_file` | `Glob`, `find` | `limit=20`; `paths=["!**/test/**"]` to exclude |
| Find files by name | `mcp__idea__find_files_by_name_keyword` | `Glob`, `find -name` | `fileCountLimit=20` |
| Find symbol definition | `mcp__idea__search_symbol` | manual grep | `limit=10`; returns full code span |
| Open file in editor | `mcp__idea__open_file_in_editor` | n/a | use during planning for user review |
| Read file content | `mcp__idea__get_file_text_by_path` | `Read`, `cat` | `truncateMode="END"`, `maxLinesCount=200` |
| Rename/refactor | `mcp__idea__rename_refactoring` | manual find-and-replace | updates all references project-wide |
| Reformat file | `mcp__idea__reformat_file` | n/a | run after creating/editing `.java`/`.kt` |
| Search content (regex) | `mcp__idea__search_regex` | `Grep -E` | `limit=20`; `paths` supports `!` excludes |
| Search content (text) | `mcp__idea__search_text` | `Grep`, `grep` | `limit=20`; `paths=["src/**","!**/test/**"]` |
| See open files | `mcp__idea__get_all_open_file_paths` | n/a | reveals active editor context |
| Symbol info / KDoc | `mcp__idea__get_symbol_info` | manual grep | requires `filePath`, `line`, `column` |

**Rules:**
- **Always** pass `projectPath=<repo-root>` to every MCP tool call. Resolve once per session via `git rev-parse --show-toplevel`.
- **Always** call `mcp__idea__get_file_problems` after editing any `.java` or `.kt` file.
- **Always** evaluate token usage — prefer tools that return coordinates/snippets (`search_text`, `search_regex`, `search_symbol`) over full-file reads when locating code.
- **Prefer** `mcp__idea__search_text` / `mcp__idea__search_regex` over `search_in_files_by_text` / `search_in_files_by_regex` — they support `paths` glob filters with `!` excludes and return precise coordinates.
- **Prefer** `mcp__idea__search_file` over `find_files_by_glob` — supports `!` exclude patterns in `paths`.
- **Prefer** `mcp__idea__search_symbol` over grep for finding class/method/field declarations — returns full code spans.
- **Fall back** to bash/built-in tools when an MCP tool fails or has a known limitation.
- File paths passed to MCP tools must be **relative to the repo root** (e.g. `src/main/java/com/example/MyClass.java`).
- During planning sessions, use `mcp__idea__open_file_in_editor` to open files in IntelliJ IDEA for interactive user review.
- **IP/security:** All tools in this table operate exclusively within the local IntelliJ IDEA process — no source code is transmitted to external services as part of tool execution. Tool results are sent to the Claude API as part of normal Claude Code operation. Do **not** use `mcp__idea__` tools outside this table without first verifying they do not call external services (e.g. JetBrains AI features).

## Build, Test, and Project Operations — Use Maven by Default

For build, test execution, and project structure queries, **use Maven commands by default**:

| Task | Default (Maven) | IDEA alternative (opt-in) |
|------|-----------------|--------------------------|
| Build project | `mvn clean install` | `mcp__idea__build_project` |
| Run tests | `mvn test` | `mcp__idea__execute_run_configuration` |
| Run tests (single module) | `mvn test -pl <module-path>` | `mcp__idea__execute_run_configuration` |
| Run integration tests | `mvn verify -pl <module-path>` | `mcp__idea__execute_run_configuration` |
| List project modules | `mvn help:evaluate -Dexpression=project.modules` | `mcp__idea__get_project_modules` |
| Project dependencies | `mvn dependency:tree` | `mcp__idea__get_project_dependencies` |
| Run service (dev mode) | `mvn quarkus:dev` (from service directory) | `mcp__idea__execute_run_configuration` |
| Build über-jar | `mvn package -Dquarkus.package.type=uber-jar` | n/a |

The IDEA alternatives may be used when the user explicitly requests it (e.g. "use IDEA to build") or when a specific
run configuration is needed that has no Maven equivalent.

## Project Architecture

This is a monorepo of microservices for Par Bricole — an organization platform managing groups, mail, calendar, and invoicing. Primary language is **Kotlin** on **Java 21**, built with **Quarkus 3.x** and **Maven**.

### Module Structure

| Module | Purpose | Deployment |
|--------|---------|-----------|
| `services-bom/` | BOM POM — all dependency versions live here | n/a |
| `security/` | Keycloak/OIDC auth via Elytron + Quarkus security | library |
| `shared/` | Cross-cutting conventions and standards | library |
| `organisation/` | Groups, memberships, roles | WAR on WildFly |
| `integration/` | External integrations (mail, calendar, invoicing, secretariat, ARX) | Quarkus apps |
| `cards/` | Card management — mostly dormant | n/a |

Module naming follows `<domain>-<layer>-<impl>`:
- `-transport-model` — REST DTOs
- `-spi-*` — Service Provider Interface (port/contract)
- `-dao-impl-*` — Data access implementations (`jdbc` or `jdbi`)
- `-application` — Quarkus deployable service

### Hexagonal Architecture (integration services)

All integration services follow Ports & Adapters. Canonical package structure (defined in `integration/integration-spi-common/`):

```
<bounded-context>/
  domain/                                    # Pure domain models — no frameworks
  application/                               # Use case orchestration via ports — no frameworks
  infrastructure/
    adapter/
      inbound/rest/                          # JAX-RS endpoints, parameter converters
      outbound/persistence/                  # Port interfaces used by application layer
    persistence/
      entity/                                # Persistence entities
      mapper/                                # Entity ↔ domain mappers
      repository/                            # JDBI repository implementations
```

> Old code uses `bizlogic/` naming — deprecated. New code uses `application/`.

Shared helpers (`messaging/`, `persistence/`, `properties/`) in `integration-spi-common` are reusable across all integration services and live outside hexagonal boundaries intentionally.

### Integration Services and Ports

| Service | Port | External System |
|---------|------|----------------|
| `calendar/google-calendar-application` | 8090 | Google Calendar API |
| `mail/ilait-mail-application` | 8091 | Ilait mail provider (IMAP bounce reading, bulk mail) |
| `invoicing/hostek-invoicing-application` | — | Hostek invoicing provider |
| `secretariat/secretariat-application` | — | Secretariat data pump |

### Inter-service Communication

- **REST (JAX-RS)** — primary service-to-service and client communication
- **JMS via Apache Artemis** — asynchronous messaging between services
- **Shared PostgreSQL** — services share a database with `currentSchema=integration`

## Configuration Profiles

Quarkus `%`-prefixed profiles in `application.properties`:
- `%dev` — local development; Keycloak at `localhost:8180`
- `%test` — test execution
- `%staging` — staging environment
- (no prefix) — production

Key environment variables:
- `SERVICEDB_HOST`, `SERVICEDB_PORT`, `SERVICEDB_UID`, `SERVICEDB_PWD` — database
- `BULKMAIL_SERVER`, `BULKMAIL_UID`, `BULKMAIL_PWD` — Ilait mail provider
- `CALENDAR_PROTOCOL`, `CALENDAR_HOST`, `CALENDAR_URL_PREFIX`, `CALENDAR_CREDENTIALS` — Google Calendar

## Package Naming

All production code lives under `se.parbricole.services.*`, mirroring the module structure:
- `se.parbricole.services.integration.*`
- `se.parbricole.services.organisation.*`
- `se.parbricole.services.security.*`