<!--
Written on 2026-11-11 by Claude Fable 5.2, asked by the session building this experiment for the
shape domain-driven design's best advocate would defend. It read only `refactor/base`, without
seeing either refactor branch. Kept as written. Checked against the code since: `Spell` is defined
three times, not twice (`spell/`, `subclass/`, and inside `monster/domain/Monster.kt`). The collision
with the 2014-only scope decision is real, and is still open in both branches' requirements v3.
A proposal: nobody who can decide has accepted it. Two edits since: the mission is cited with its
version, and the proposed shape items are numbered P1 to P5, so they are not mistaken for
this repository's `SHAPE` requirements.
-->

# A domain-driven shape for dnd-5e-api-kotlin

Proposal, not a decision. Basis: I read about 25 of the 369 files on `refactor/base` (docs, the
spell, class, level, monster, subclass, feature packages, `shared/`, the converters) and upstream's
class and level JSON. I ran nothing. Claims about the other 19 resource packages are inferred from
their identical file names. Paths below are under `src/main/kotlin/com/thejaxonhill/dnd5eapi/`.

## 1. What the domain is for, and its subdomains

The purpose (REQ1/MISSION-1) is to let builders of character tools and AI agents ask the SRD questions.

| Subdomain | Class | Why |
| --------- | ----- | --- |
| **Compendium**: publishing SRD entries by kind and index | Supporting | Necessary, but the knowledge is upstream's. We add no rule to it. |
| **Character rules**: answers computed from entries (multiclass eligibility is the first) | Core, if the product goes there | The only place this project would know something upstream's JSON does not say outright. |
| Paging, HTTP, MCP, GraphQL, OpenAPI, storage | Generic | Bought from Spring. |

Honest view: today there is **no core subdomain in the code**. Tactical DDD (aggregates, a
repository and use cases per entity) earns nothing on a read-only copy of someone else's data.
Strategic DDD does earn its cost, in three places: naming the language, stating the relationship to
upstream once, and giving the first real rule a home that does not depend on storage or upstream's
spelling. I am fairly sure of this. It is the main claim.

## 2. Ubiquitous language

Keep the SRD's words: Class, Subclass, Race, Subrace, Spell, Feat, Feature, Trait, Proficiency,
Ability Score, Choice, Option. Add: **Entry** (one SRD item), **Kind** (spells, monsters, ...),
**Index** (an entry's identity within its kind), **Reference** (kind + index + name pointing at
another entry), **Compendium** (all entries).

Terms the code uses wrongly or twice:

- **Resource** is REST's word for Kind. Fine at the surface, not in the model.
- **Example** (`spell/domain/Spell.kt`: `SpellExample`) is Spring Data's query-by-example, placed
  in the domain. The domain word is **Filter**.
- **id** and **updatedAt** on every domain class are not SRD facts. `id` is Mongo's key;
  `updated_at` is stamped by `scripts/seed.py`. Identity is Index.
- **Level** means a class's progression row (`barbarian-1`), a spell's level, and a character's
  level. `level/domain/Level.kt` has a field `level` inside `Level`. Say **Class Level**.
- **Spell** names two things: `spell/domain/Spell.kt` and the subclass spell grant in
  `subclass/domain/Subclass.kt`. `SpellPrerequisite`, `Damage`, and `ActionOption` are likewise each
  defined twice with different fields (`shared/domain/model/` versus `subclass/`, `spell/`, `monster/`).
- **Prerequisite** has four unrelated shapes: `shared/domain/model/Prerequisite.kt`,
  `MultiClassingPrerequisite` in `characterclass/domain/CharacterClass.kt`,
  `ScorePrerequisiteOption`, and the subclass one.
- **Rule** (`rule/`) is a chapter of prose, not something evaluated. Change 4 adds the first
  evaluated rule; call it **Eligibility** so the two do not collide.
- **ApiReference**, **Page**, **PagedModel** sit in `shared/domain/model/` but are delivery words.
  `ApiReference.url` carries upstream's path (`/api/2014/...`), which this API cannot serve (FINDINGS).
- **View** is the domain class minus `id` and `updatedAt` (`spell/application/dto/SpellView.kt`).

## 3. Bounded contexts and context map

Three contexts, not 25. The 25 packages share one language and one `shared/domain/model`; they are
modules of one context.

```
5e-database (upstream)  --conformist-->  Compendium  --ACL-->  Character Rules
                                             |                      |
                                   Open Host Service: REST, MCP, GraphQL (published language)
```

- **Upstream to Compendium: Conformist, with translation of names at one seam.** The golden master
  holds our output to upstream's shape, `desc` and upstream URLs included. So our model of an entry
  *is* upstream's. An anticorruption layer here would translate upstream into itself. What must be
  isolated is spelling (collection names, field names, the `option_type` tags), in one adapter.
  This disagrees with the row for `refactor/ddd` in `docs/EXPERIMENT.md`, which puts the ACL at
  this border. I think the best advocate would not defend that placement.
- **Compendium to Character Rules: Anticorruption Layer**, customer–supplier. Rules need a model
  that is ours: `AbilityScores`, `MulticlassRequirement`. The ACL turns a class entry's
  `multi_classing` into that, and nothing else crosses.
- **Surfaces: Open Host Service.** The JSON shape and tool names are the published language and a
  one-way door (`docs/DESIGN_v1.md`).

## 4. Aggregates and invariants

**Compendium: no aggregates.** An aggregate protects invariants across changes, and nothing
changes. Each Entry is an immutable value identified by (Kind, Index) that points at others by
Reference. What look like invariants (index unique within a kind, a Choice's `choose` no larger
than its options, references that resolve) are checks on a feed. They belong at load, as a refusal
or a finding, not in constructors. This matters: 25 "domain" classes modelled as entities imply a
lifecycle that does not exist, and the `!!` in `MongoSpellRepository.kt` turns a missing upstream
field into a crash at request time.

**Character Rules: value objects and a pure function, still no aggregate.**

- `AbilityScores`: all six abilities present, each an integer in a stated range. The range is a
  decision for the owner (inferred: 1–30).
- `MulticlassRequirement`: either *all of* a non-empty list of (ability, minimum), or *choose n of*
  options with 1 ≤ n ≤ count. Upstream has both: fighter uses `prerequisite_options`, the other
  eleven use `prerequisites` (read from `5e-SRD-Classes.json`).
- `eligibility(scores, requirement)` returns eligible, or the unmet minimums.

## 5. Repositories and ports

- **One driven port: the Compendium.** `entry(kind, index)`, `entries(kind, filter, page)`, and
  name search for equipment. It stands for "the SRD as loaded", not for a table. The 25 interfaces
  like `spell/domain/SpellRepository.kt` state this one idea 25 times.
- **State owner:** upstream. MongoDB is a copy with no stated age (`docs/DESIGN_v1.md`). The
  application owns none. So no `save`.
- **Driven adapters:** MongoDB today; in-memory JSON for change 5. Upstream's spelling lives here.
- **Driving ports:** get entry, list entries, search equipment, check eligibility.
- **Driving adapters:** REST controllers, MCP tools, GraphQL.
- **Rules' port:** `requirementFor(classIndex)`, implemented by the ACL over the Compendium.

## 6. Shape statement

- **P1** *(judged, proposed)* There are two models. The Compendium conforms to 5e-database: an entry is
  upstream's entry, served unchanged. Character Rules has its own model and learns of the
  Compendium only through a translation it owns.
- **P2** *(judged, proposed)* Upstream's spelling (collection names, field names, type tags) appears in
  one adapter and nowhere else.
- **P3** *(judged, proposed)* The Compendium is one read-only port. Kinds are data given to it, not
  packages. A type exists for an entry only where a rule or a surface reads its fields.
- **P4** *(judged, proposed)* A rule is a pure function over value objects that hold their own
  invariants. It imports no framework, no storage, and no entry type.
- **P5** *(judged, proposed)* Every surface is an adapter over the same driving ports, so the same
  question gets the same answer on each.

Judge for all five: the shape's holder, not yet named.

## 7. The five changes

1. **26th resource.** One new Kind in the Compendium's list; the surfaces follow. Cost to raise
   first: it is 2024 data, and the scope decision says 2014. Edition becomes part of identity.
2. **Upstream rename.** Lands only in the driven adapter (P2). Our word stays `desc`, because
   the published language is frozen. Today the prefix is in 25 `@Document` annotations.
3. **GraphQL.** A third driving adapter (P5). Cost: GraphQL wants field types, and P3
   withholds them. Either a JSON scalar or generated types; the owner decides. This is the shape's
   weakest point.
4. **Multiclass eligibility.** Lands whole in Character Rules: two value objects, one function, one
   ACL function, one adapter per surface. The shape protects it from changes 2 and 5. This is where
   DDD wins. Unstated inputs to decide: a missing score, an out-of-range score, an unknown class.
5. **Replace MongoDB.** A second driven adapter. The port protects surfaces and rules. It does not
   reduce the real work: filter matching, sort, paging, and file order were Mongo's behaviour and
   are now the product (FINDINGS, insertion order). The port must state them in domain terms first.

## 8. Agreement and disagreement with the current code

Agrees:

- Dependencies point inward; controllers and tools are thin adapters over use cases.
- The repository interface is declared by the domain and implemented outside it.
- `shared/infrastructure/persistence/converter/ChoiceReadingConverter.kt` decodes upstream's tagged
  unions into sealed types. This is real translation work, and the most valuable code here.

Disagrees:

- Contexts are drawn per entity, so the one seam to upstream is spread over 25 `*Document` classes,
  a global `SnakeCaseFieldNamingStrategy` in `application.yml`, and the converters.
- Domain, document, and view are three copies of one shape with hand-written mappers. No rule
  distinguishes them. `CharacterClass` is nearly all nullable.
- The domain is not framework-free: `@Document` in `shared/domain/model/Choice.kt`, Jackson's
  `@get:JsonProperty` in `level/domain/Level.kt` and `subclass/domain/Subclass.kt`.
- All 25 repositories declare `save`; no use case calls it, and writes are a non-goal.
- `abilityscore/application/port/AbilityScoreImageProvider.kt` is a port with no caller found.
- There is no place for a rule. `MultiClassing` is carried as data that nothing reads.
