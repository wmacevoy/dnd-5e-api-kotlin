# Design — v2 (branch `refactor/ddd`)

How we intend to satisfy the requirements, and what we rejected. Every mechanism here should be
replaceable without renegotiating a requirement
([SE1/R5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md#r5)).

Written on 2026-11-11 for the shape in `REQUIREMENTS_v2.md` (`REQ2/SHAPE-1`, `REQ2/SHAPE-3`). It
replaces v1 on this branch only; `DESIGN_v1.md` describes `refactor/base`. The structure is Jackson
Hill's, kept and tightened. Its reasons are this document's reading of his position, and he has not
reviewed them yet.

## Bounded contexts

| Context | Resources | Its vocabulary |
| ------- | --------- | -------------- |
| `character` | ability scores, alignments, backgrounds, races, subraces, traits, languages, skills, proficiencies, feats | What a character is made from at creation |
| `progression` | classes, subclasses, features, levels | What a character gains as it advances |
| `spellcasting` | spells, magic schools | Magic |
| `equipment` | equipment, equipment categories, magic items, weapon properties | What a character carries |
| `bestiary` | monsters | What a character meets |
| `rules` | rules, rule sections, conditions, damage types | The game's reference text and shared effects |

`shared/` is the shared kernel. It holds the value types every context uses (`Index`, `ApiReference`,
`Choice` and its options, `Damage`), plus paging and the Mongo converters for those shared types.

## Layers, in each context

| Layer | Holds | Contract (one line) |
| ----- | ----- | ------------------- |
| `domain` | Our model, the filter a list accepts (`*Example`), and the repository port | Types with no storage fields; `Index` is the identifier |
| `application` | One `*Queries` service per resource, and the views (`dto/*View`) | `list(page, example)` and `byIndex(text)`; an unknown or malformed index throws `NoSuchElementException` |
| `infrastructure/delivery` | REST controllers and MCP tools | HTTP or an MCP call to a query service |
| `infrastructure/persistence` | Documents, Spring Data repositories, and the translators to domain types | The anticorruption layer: upstream's schema stops here |

A layer depends only on layers below it. `GlobalExceptionHandler` maps `NoSuchElementException` to
404 with an empty body, other runtime exceptions to 400, and the rest to 500.

## How one request reaches the data

`GET /api/spells?name=Acid+Arrow` reaches `SpellController.getSpells`
(`spellcasting/infrastructure/delivery`), which binds the page and a `SpellExample`, and calls
`SpellQueries.list`. The query service asks the `SpellRepository` port. Its adapter,
`MongoSpellRepository`, builds a `SpellDocument` probe, queries `2014-spells` through Spring Data,
and translates each `SpellDocument` into a `Spell` with `toDomain()`. The query service turns each
`Spell` into a `SpellView`, and the controller returns them in a `PagedModel`. An MCP call enters at
`SpellTools` and takes the same path.

## State

| State | Owner (authoritative) | Copies | Max staleness | While wrong, the user sees |
| ----- | --------------------- | ------ | ------------- | -------------------------- |
| SRD data | 5e-bits/5e-database | The local MongoDB, one copy | Until someone reloads it | Old or missing rules, with no indication |

## Essential complexity

- 26 resources served the same way — `REQ3/API-1`, `REQ1/API-2`, `REQ1/API-3`
- Polymorphic SRD choices and options, translated by the converters in
  `shared/infrastructure/persistence/converter` — `REQ3/DAT-1`
- Two delivery surfaces over the same query services — `REQ3/API-1`, `REQ3/MCP-1`

## One-way doors

Unchanged from v1. The published REST paths and JSON shapes, the MCP tool names, and the dependency
on 5e-database's collection names and fields.

## Decisions

- **Bounded contexts over one package per resource** — `REQ2/SHAPE-1` — so related resources share a
  vocabulary and a reader finds them together — rejected: 25 sibling packages, which is v1.
- **Three representations: document, domain, view** — `REQ2/SHAPE-3` — so an upstream change stops
  in the translator, and a change to our model does not reach clients unless a view changes —
  rejected: serving upstream's documents directly (`refactor/projection`).
- **One query service per resource** — `REQ2/SHAPE-1` — two reads of one resource belong together,
  and 51 single-function classes said less than 25 small ones — rejected: one use case per class (v1).
- **`Index` as a value object** — `REQ2/SHAPE-3`, `REQ1/API-4` — an identifier with a rule, checked at
  the edge of the model. The rule was written from upstream's data: the first guess refused 10 real
  indexes. `parse` returns nothing for text that cannot be an index, so a lookup still answers 404.
- **Kept, as compromises** — Jackson annotations remain on some domain types (`@JsonValue` on the
  shared enums, `@JsonProperty("class")` on `Level` and `Subclass`). Views reuse nested domain types,
  and MCP serializes with Spring AI's own mapper, so moving the annotations out would change MCP
  output, or would need a view type for every nested type. `Page` and `PagedModel` sit in the shared
  kernel because the repository ports take and return them.
- **Tests run against a live, seeded MongoDB** — unchanged from v1.

## Deferred

- Value objects for dice and for a `Choice`'s count against its options. No rule needs them yet, and
  an invariant nobody relies on is speculative structure. Trigger: change 4, the multiclassing rule,
  which reads ability scores and prerequisites.
- A mechanical check of the dependency rule. Trigger: a layering violation that costs someone time.
