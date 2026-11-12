# Design — v2 (branch `refactor/ddd`)

How we intend to satisfy the requirements, and what we rejected. Every mechanism here should be
replaceable without renegotiating a requirement
([SE1/R5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md#r5)).

Written on 2026-11-11 for the domain-driven shape in `REQUIREMENTS_v2.md` (`REQ2/SHAPE-1`,
`REQ2/SHAPE-3` to `REQ2/SHAPE-6`), following `PROPOSAL_DDD_SHAPE.md`. It replaces v1 on this branch
only; `DESIGN_v1.md` describes `refactor/base`.

## Context map

```
5e-database --conformist--> Compendium --open host service--> REST, MCP clients
                                 |
                                 +--anticorruption layer--> Character Rules   (change 4; `rules/`)
```

| Context | Subdomain | Owns | Built |
| ------- | --------- | ---- | ----- |
| Compendium | Supporting: serving SRD entries | Nothing: upstream owns the data, and this only reads a copy | Yes |
| Character Rules | Core, when its first rule arrives | Its own model: ability scores, multiclassing requirements | Yes: multiclassing (change 4) |

## Components

| Where | Role | Contract (one line) | Imports a framework? |
| ----- | ---- | ------------------- | -------------------- |
| `compendium/Kind.kt` | Model | The kinds, their nouns, filters, and whether they can be searched by name | No |
| `compendium/Entry.kt` | Model | `Entry`, `Index` (with its rule), `PageRequest` (with its rule), `Order`, `EntryPage` | No |
| `compendium/Compendium.kt` | Driven port, and the query service | `Compendium` reads entries; `CompendiumQueries` answers `entry`, `entries`, `entriesNamed` | No |
| `adapter/upstream/MongoCompendium.kt` | Driven adapter | Implements `Compendium`; upstream's collection names, snake_case, and storage fields stop here | Spring Data |
| `adapter/published/PublishedLanguage.kt` | Open host service | An entry as clients receive it: the departures from upstream, and the paged list | No |
| `adapter/rest/RestApi.kt` | Driving adapter | Registers routes per kind; REST's paths, tags, summaries, and equipment's default order | Spring |
| `adapter/mcp/McpTools.kt`, `McpNulls.kt` | Driving adapter | A list and a get tool per kind; MCP's tool names, argument shapes, and nulls | Spring AI |
| `adapter/Wiring.kt` | Wiring | Builds `CompendiumQueries`, so the model needs no annotation | Spring |
| `rules/Eligibility.kt` | Model | `AbilityScores`, `Multiclassing`, and `evaluate`: the multiclassing rule, pure | No |
| `rules/FromCompendium.kt` | Anticorruption layer | Reads a class's `multiClassing` as the Compendium spells it; refuses an option type it does not evaluate | No |
| `rules/CharacterRules.kt` | Query service | `multiclassing(index, scores)`, asked through `CompendiumQueries` as REST and MCP both do | No |
| `adapter/rest/MulticlassingController.kt` | Driving adapter | `GET /api/classes/{index}/multiclassing`, the six scores as query parameters named by ability index | Spring |
| `GlobalExceptionHandler.kt` | Error mapping | `NoSuchElementException` to 404 with an empty body, other runtime exceptions to 400, the rest to 500 | Spring |

## How one request reaches the data

`GET /api/spells?name=Acid+Arrow` reaches the `Endpoint` that `RestApi` registered for the `spells`
kind. It turns the request's page into a `PageRequest`, keeps the kind's filters that the request
names, and asks `CompendiumQueries.entries`. That asks the `Compendium` port. `MongoCompendium` reads
the `2014-spells` collection with `name = "Acid Arrow"` and returns entries in the Compendium's
spelling. `published` applies the kind's departures, and `paged` wraps them. An MCP call enters at
`McpTools` and takes the same path, then adds MCP's nulls.

## Where each fact lives, and why

| Fact | Lives in | Because it belongs to |
| ---- | -------- | --------------------- |
| A kind exists; its nouns, filters, searchability | `compendium/Kind.kt` | The Compendium's language |
| Its collection name, snake_case, `_id`, `updated_at` | `MongoCompendium.kt` | Upstream's spelling (`REQ2/SHAPE-4`) |
| Fields that differ from upstream today | `PublishedLanguage.kt` | What clients were promised (`REQ2/SHAPE-4`) |
| Path, tag, summaries, equipment sorted by name | `RestApi.kt` | REST |
| Tool names, argument shapes, nulls | `McpTools.kt`, `McpNulls.kt` | MCP |
| Multiclassing rules as upstream spells them | `rules/FromCompendium.kt` | Upstream's spelling (`REQ2/SHAPE-4`) |
| Whether a character meets them | `rules/Eligibility.kt` | Character Rules' language (`REQ2/SHAPE-5`) |

A new kind is one row in `KINDS`, when its nouns give its tool names and its edition and name give
its collection. A fact that breaks a pattern is written where its owner lives.

## State

| State | Owner (authoritative) | Copies | Max staleness | While wrong, the user sees |
| ----- | --------------------- | ------ | ------------- | -------------------------- |
| SRD data | 5e-bits/5e-database | The local MongoDB, one copy | Until someone reloads it | Old or missing rules, with no indication |

No aggregates: nothing here changes state (`REQ2/SHAPE-5`).

## Decisions

- **Two contexts by purpose, one built** — `REQ2/SHAPE-1` — the Compendium is supporting and kept
  plain, and Character Rules waits for its first rule — rejected: one context per resource
  (`refactor/layered`), and an empty Character Rules package now (speculative structure).
- **Conform to upstream; entries have no type** — `REQ2/SHAPE-4`, `REQ2/SHAPE-5` — an anticorruption
  layer would translate upstream into itself, because the output is held to upstream's shape. Types
  wait for a rule that reads fields. The cost: the OpenAPI document has no response schemas.
- **Departures belong to the published language, not to storage** — `REQ2/SHAPE-4` — they are a
  promise to clients, not a fact about upstream. So a client-approved fix is a change to the
  published language, and an upstream rename is a change to the upstream adapter.
- **One query service for both surfaces** — `REQ2/SHAPE-6` — so REST and MCP give the same answer to
  the same question.
- **`PageRequest` keeps the paging library's rules and words** — `REQ2/SHAPE-5` — a negative page is
  refused as Spring refused it before, now as an invariant of a value the model owns.
- **Multiclassing is computed per request, not stored** — `REQ2/SHAPE-5` — a pure function over the
  class's rules and the scores, so nothing can go stale. When a choice is not met, every option is
  listed as unmet. A prerequisite option of a type the rule does not evaluate is refused, not skipped.
- **Tests run against a live, seeded MongoDB** — unchanged from v1.

## Deferred

- Response schemas in the OpenAPI document. Trigger: a client asks, or the experiment's schema
  measure is reported.
