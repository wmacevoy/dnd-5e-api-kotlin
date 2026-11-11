# Design — v1

How we intend to satisfy the requirements, and what we rejected. Every mechanism here should be
replaceable without renegotiating a requirement
([SE1/R5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md#r5)).

Rebuilt on 2026-11-11 from the code's structure
([SE1/W12](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w12)). The
structure below was read from the code. Reasons were not recorded anywhere, so each is *(inferred)*.

## Components and the dependency rule

Each of the 25 resource packages has the same layers. `shared/` holds common models, converters, and
configuration.

| Component | Layer | Kind | Contract (one line) | Holds state? |
| --------- | ----- | ---- | ------------------- | ------------ |
| `infrastructure/delivery/*Controller` | 3 | REST adapter | HTTP to use case; returns views | No |
| `infrastructure/delivery/*Tools` | 3 | MCP adapter | MCP tool call to use case; returns views | No |
| `infrastructure/persistence/Mongo*Repository` | 3 | Persistence adapter | Implements the domain repository over MongoDB | No |
| `application/usecase/*` | 2 | Use case | Loads through the domain repository; maps to a view; missing item throws `NoSuchElementException` | No |
| `application/dto/*View` | 2 | Output shape | The item as clients see it | No |
| `domain/*`, `domain/*Repository` | 1 | Model and port | The entity, its filter, and the storage interface | No |
| `GlobalExceptionHandler` | 3 | Error mapping | `NoSuchElementException` to 404, other runtime exceptions to 400, the rest to 500 | No |
| MongoDB `5e-database` | External | Data store | Collections named `2014-*` | Yes |

A component may depend only on strictly lower layers. Nothing enforces this yet. Controllers do reach
sideways to `shared/infrastructure/persistence/converter` for `toPage`.

## State

| State | Owner (authoritative) | Copies | Max staleness | While wrong, the user sees |
| ----- | --------------------- | ------ | ------------- | -------------------------- |
| SRD data | 5e-bits/5e-database | The local MongoDB, one copy | Until someone reloads it | Old or missing rules, with no indication |

The application keeps no other state.

## Essential complexity

- 25 resources served the same way — `REQ1/API-1`, `REQ1/API-2`, `REQ1/API-3`
- Polymorphic SRD choices and options, read by the converters in `shared/infrastructure/persistence/converter` — `REQ1/DAT-1`
- Three delivery surfaces over the same use cases — `REQ1/API-1`, `REQ1/MCP-1`, `REQ1/GQL-1`

## One-way doors

No review of any of these is recorded.

- The published REST paths and JSON shapes, once clients depend on them.
- The MCP tool names, once agents depend on them.
- The dependency on 5e-database's collection names and fields, which this project does not control.

## Decisions

- **One package per resource, each with domain, application, and infrastructure layers** —
  `REQ1/API-1` — *(inferred)* to keep 25 similar resources uniform and each delivery surface thin —
  rejected alternatives not recorded — source: the commits of 2026-02-15, from 09fe61d to 415a143.
- **Separate domain, document, and view classes for each resource** — `REQ1/DAT-1` — *(inferred)* to
  keep 5e-database's shape from reaching clients — rejected alternatives not recorded — source: 64a9507, 697c307.
- **Use `2014-*` collections** — `REQ1/DAT-1` — *(inferred)* to match a change in 5e-database's
  naming — rejected alternatives not recorded — source: 86907fb.
- **Tests run against a live, seeded MongoDB** — *(inferred)* simplest way to exercise real data —
  rejected alternatives not recorded — source: `ControllerTest`.

## Deferred

- Not yet: a mechanical check of the dependency rule — nobody has asked for one; trigger: a layering
  violation that costs someone time.
