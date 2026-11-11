# Design — v2 (branch `refactor/projection`)

How we intend to satisfy the requirements, and what we rejected. Every mechanism here should be
replaceable without renegotiating a requirement
([SE1/R5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md#r5)).

Written on 2026-11-11 for the shape in `REQUIREMENTS_v2.md` (`REQ2/SHAPE-1`, `REQ2/SHAPE-3`). It
replaces v1's layering on this branch only. `DESIGN_v1.md` describes `refactor/base`.

## Components

All of the code is in one package, `com.thejaxonhill.dnd5eapi`.

| File | Kind | Contract (one line) | Touches I/O? |
| ---- | ---- | ------------------- | ------------ |
| `Resources.kt` | Data | One row per resource: path, collection, tag and nouns, tool names, filters, list shape, departures, MCP nulls | No |
| `Projection.kt` | Pure functions | A stored document to a REST item (`item`) or an MCP item (`mcpItem`); a page of items to a paged list | No |
| `Store.kt` | Data access | A page of documents by exact-match filters, one by `index`, or a page by name substring | MongoDB |
| `RestApi.kt` | REST surface | Registers `GET /api/{path}`, `/{index}`, and equipment's `/search` for each row, and writes their OpenAPI paths | HTTP |
| `McpTools.kt` | MCP surface | Builds a list tool and a get tool for each row, with the input schemas Spring AI used to generate | MCP |
| `GlobalExceptionHandler.kt` | Error mapping | `NoSuchElementException` to 404 with an empty body, other runtime exceptions to 400, the rest to 500 | No |

## How one request reaches the data

`GET /api/spells?name=Acid+Arrow` goes to `Endpoint.list`, registered for the `spells` row by
`RestApi.registerRoutes`. It keeps the row's filters that the request names, and calls
`Store.find`, which queries the `2014-spells` collection with `name = "Acid Arrow"` and the page.
Each document goes through `item`, which drops `_id` and `updated_at`, camelCases the keys, drops
nulls, and applies the row's departures. `paged` wraps the items with the page. An MCP call takes
the same path through `McpTools.kt`, and uses `mcpItem` instead.

## State

| State | Owner (authoritative) | Copies | Max staleness | While wrong, the user sees |
| ----- | --------------------- | ------ | ------------- | -------------------------- |
| SRD data | 5e-bits/5e-database | The local MongoDB, one copy | Until someone reloads it | Old or missing rules, with no indication |

The application keeps no other state.

## Essential complexity

- 26 resources served the same way: one row each — `REQ3/API-1`, `REQ1/API-2`, `REQ1/API-3`
- Two delivery surfaces, generated from the same rows — `REQ3/API-1`, `REQ3/MCP-1`
- Today's output, which departs from upstream in about 40 ways across 8 resources. Each departure is
  a line in that resource's row, and most of them lose data. They are kept until someone decides to
  fix them, because the golden master holds today's output (`EXPERIMENT.md`, rule 3).

## One-way doors

Unchanged from v1. The published REST paths and JSON shapes, the MCP tool names, and the dependency
on 5e-database's collection names and fields.

## Decisions

- **A resource is a row, not a package** — `REQ2/SHAPE-1` — to test whether the layering was
  essential (`EXPERIMENT.md`) — rejected: one package per resource with three layers, which is v1.
- **Items are maps, not classes** — `REQ2/SHAPE-3` — the API returns upstream's documents, so a class
  per resource would restate upstream's schema — rejected: typed views, which v1 has. The cost: the
  OpenAPI document has no response schemas.
- **Departures and MCP nulls are data in the row** — `REQ2/SHAPE-3` — so every way the output differs
  from upstream can be read and counted in one file — rejected: reproducing them with classes.
  They were found by diffing upstream's JSON against the golden master's bodies.
- **Routes are registered from the registry at startup** — `REQ2/SHAPE-1` — so a new row needs no new
  code, and unknown paths behave as before (HTTP 500) — rejected: one controller with an
  `/api/{path}` pattern, which would answer for paths that are not resources.
- **Tests run against a live, seeded MongoDB** — unchanged from v1.

## Deferred

- Response schemas in the OpenAPI document — trigger: a client asks for them, or the experiment's
  schema measure is reported. The registry could describe them, or upstream's own schemas could be used.
