<!--
Written on 2026-11-11 by Claude Fable 5.2, asked for a generic guide to a domain-driven shape and
its alternatives, after it wrote `PROPOSAL_DDD_SHAPE.md` for this project. Not specific to this
project, and nothing in it was measured. One edit since: the template's placeholders are lettered
(SHAPE-A to SHAPE-F), so they are not read as this repository's requirements. It may belong in the
software-engineering guide rather than here.
-->

# Design shapes: a domain-driven shape, and its alternatives

A shape is a few statements about a system that a reviewer can hold code against. It is not a
folder layout. Its job is to make change simple to think about (SE1/D8, SE1/D9): a reader should
guess where a change lands before opening a file. Start from what the system is for. Counts of
files and layers come last, and often do not matter.

Basis: general practice and one worked case, written by an agent. Nothing here was measured. Treat
each rule as a claim to argue with.

## Part 1. A domain-driven shape

### The questions, in order

Answer each in a sentence or a small table before any code layout. A later answer that
contradicts an earlier one means the earlier one was wrong; go back.

**1. Purpose.** Who asks this system what, and what would they lose without it?
- Good: one sentence naming a person and their need. "Dispatchers assign the nearest free ambulance."
- Wrong: a description of the technology. "A REST service over a database."

**2. Subdomains.** Split the problem, not the code, into core, supporting, and generic.
- Core: where you know something others do not. Model it with care.
- Supporting: necessary, but no advantage. Keep it plain.
- Generic: solved elsewhere. Buy it or use a library.
- Good: a table with a *why* per row, and an honest "there is no core here yet" when that is true.
- Wrong: everything is core. Or the list is the list of tables.

**3. Language.** List the domain's words, each with one meaning. Then list the words the code uses
twice or wrongly.
- Good: a glossary the domain expert would sign. Words from storage and delivery (id, row, DTO,
  page, resource) are marked as not domain words.
- Wrong: the same word with three field lists. If *Order* means a basket, a shipment, and an
  invoice line, you have three concepts or three contexts. Decide which.

**4. Contexts and their relationships.** A bounded context is a region where one language holds.
Draw a border where a word changes meaning or a different group decides. Then name each
relationship.
- Conformist: you take upstream's model as yours.
- Anticorruption layer (ACL): you have your own model and translate at the border.
- Customer–supplier: upstream listens to you.
- Open host / published language: you publish a stable model for many consumers.
- Shared kernel: two contexts co-own a small model.
- Good: few contexts, each with a reason. Every arrow has a name and a direction.
- Wrong: one context per entity or per table. Those are modules of one context.
- Wrong: an ACL where you conform. Test: after translation, is your model different from
  upstream's? If not, there is nothing to protect. Isolate upstream's *spelling* in one adapter
  and call the relationship conformist.

**5. State and its owner.** List every piece of state: who owns it, where copies live, how stale a
copy may be (SE1/D1, SE1/D2).
- Good: "Upstream owns the catalogue. Our database is a copy, refreshed nightly. We own carts."
- Wrong: assuming you own whatever is in your database. A copy of someone else's data has no
  lifecycle in your system.

**6. Invariants and aggregates.** An invariant is a rule that must hold after every change. An
aggregate is the smallest cluster that must change together to keep one.
- Good: each aggregate names the invariant it protects. "Booking: no seat sold twice per flight."
- Good: where nothing changes, say "no aggregates". Immutable values and load-time checks are enough.
- Wrong: every entity is an aggregate with a repository and a use-case class. If you cannot name
  the invariant, it is a record.
- Wrong: checking a data feed's quality in constructors. That is input validation; do it once at
  the edge and refuse or report (SE1/R6).

**7. Ports.** Driving ports are the questions and commands the outside may send. Driven ports are
what the domain needs from the world.
- Good: driving ports read as the purpose from question 1. Driven ports are named for a domain
  idea ("the catalogue as loaded"), not a table.
- Wrong: one repository interface per table, each with `save` that nobody calls. One port per
  idea.

Only now choose packages. The layout should make answers 4 to 7 visible. Many layouts can.

### When DDD earns its cost

Decide from the problem, not from the code.

It earns its cost when:
- The rules are hard, and domain experts argue about them.
- The same word means different things to different groups.
- State changes, and bad changes cost money or safety.
- The system will live for years while the rules keep moving.

It does not when:
- The system reads or republishes someone else's data.
- It is forms over tables with field-level validation.
- No domain expert exists, or the expert is the database schema.
- The hard part is technical: throughput, protocols, numerics.

The two halves separate. **Strategic** DDD (questions 1 to 5) is cheap and almost always worth an
afternoon. **Tactical** DDD (aggregates, repositories, domain events, use-case classes) is the
expensive part. It pays only inside a core subdomain with changing state. A system may use all of
the first and none of the second.

### A shape template

Fill in three to six. Each is marked *(judged)*: a named person reads the code against it. Each
is written so that a reviewer can decide from imports, the glossary, and where a diff lands,
without tracing logic.

- **SHAPE-A** *(judged)* There are *N* models: ___. Each has its own language. ___ conforms to
  ___; ___ learns of ___ only through a translation it owns.
- **SHAPE-B** *(judged)* ___'s spelling (names, tags, formats) appears in one adapter and nowhere
  else.
- **SHAPE-C** *(judged)* State ___ is owned by ___. Everything else holding it is a copy, with a
  stated age. Only ___ may change it.
- **SHAPE-D** *(judged)* The invariant "___" is kept by ___ and can be broken by no other path.
- **SHAPE-E** *(judged)* A rule in ___ is a function over domain values. It imports no framework,
  no storage, and no delivery type.
- **SHAPE-F** *(judged)* Every surface is an adapter over the same driving ports, so the same
  question gets the same answer on each.

Name the judge. Then test the shape: take five likely changes and say where each lands. A change
that lands in many places, or nowhere obvious, is the shape's weak point. Write that down too.

### Tells: the vocabulary without the purpose

- Packages named `domain`, `application`, `infrastructure`, repeated per entity.
- Three classes per concept (entity, document, DTO) with the same fields and hand-written mappers.
- "Domain" classes with nullable fields throughout and no methods.
- Repositories with `save` in a read-only system.
- Use-case classes that only forward to a repository.
- Framework or storage annotations in the domain package.
- The glossary is the list of table names.
- An ACL whose output has the same shape as its input.
- Nobody can name an invariant, or the place the next real rule would go.

## Part 2. Alternative and complementary shapes

Most of these answer a different question than DDD does. DDD says what the model is and where its
borders are. Many shapes below say how code is arranged around a model, or whether a model is
needed at all.

**Transaction script.** *Each request is one procedure, top to bottom.*
- Easy: reading one request end to end. Starting.
- Hard: rules shared between requests get copied and drift.
- Fits: few rules, many independent operations. An admin tool; a nightly export.
- With DDD: the honest alternative to tactical DDD. Right for supporting subdomains.

**Table module / active record.** *One class per table; it holds the row and its behaviour.*
- Easy: CRUD. The schema is the model, so nothing needs mapping.
- Hard: rules spanning tables. Tests need a database. The schema becomes the published interface.
- Fits: the domain really is the tables. A back-office system.
- With DDD: an alternative. Fine in supporting subdomains; poor for a core.

**Layered (n-tier).** *Presentation calls logic, logic calls data; never upward.*
- Easy: everyone knows it. Technical concerns separate.
- Hard: one feature touches every layer. Logic leaks up or down. The database ends up at the centre.
- Fits: small teams, simple domains.
- With DDD: orthogonal. Layers say where kinds of code go, not what the model is. Layering is not DDD.

**Hexagonal (ports and adapters).** *The application sits in the middle; everything outside reaches it through a port.*
- Easy: swapping storage or adding a surface. Testing without I/O.
- Hard: more interfaces. A port with one adapter and no test double is speculation.
- Fits: several surfaces or several sources. Infrastructure likely to change.
- With DDD: a complement. It protects a model but does not supply one. An empty hexagon is common.

**Clean / onion.** *Concentric rings; dependencies point inward to entities.*
- Easy: same as hexagonal, plus a fixed place for use cases.
- Hard: ceremony. One class per use case and a mapper per ring crossing.
- Fits: long-lived systems with real rules and many developers.
- With DDD: a complement, on the same terms as hexagonal. The rings do not find the entities for you.

**Vertical slices (feature folders).** *Code is grouped by feature; each slice owns its path from request to data.*
- Easy: a change stays in one folder. Deleting a feature.
- Hard: rules shared between slices. Consistency of style.
- Fits: many loosely related features. Change arrives per feature.
- With DDD: orthogonal. A slice is not a context. Slices live inside one.

**Functional core, imperative shell.** *Pure functions decide; a thin shell reads the world and writes the result.*
- Easy: testing with plain values. Reasoning about rules (SE1/D7).
- Hard: workflows that interleave I/O with decisions need restructuring.
- Fits: calculation and decision. Pricing, eligibility, scheduling.
- With DDD: a complement. A good way to write a domain model. Works with no aggregates at all.

**Data-driven (registry or projection).** *A table of data describes the cases; generic code produces the behaviour for each.*
- Easy: adding a case is adding a row. Uniformity is guaranteed, not hoped for.
- Hard: the case that does not fit. Tracing a request, since behaviour is not where you grep. The table grows flags.
- Fits: many things that differ only in data. Thirty resource kinds served the same way.
- With DDD: a complement in supporting subdomains. In a core it hides the differences that matter.

**Pipes and filters.** *Data flows through independent stages, each with one input and one output.*
- Easy: adding, removing, or reordering a stage. Testing a stage alone.
- Hard: errors and state that span stages. Interactive requests.
- Fits: transformation. Ingest, compilers, reports.
- With DDD: orthogonal. A context border is often a pipeline stage; an ACL is a filter.

**CQRS.** *Writes and reads use different models.*
- Easy: read shapes fitted to screens. A write model that only guards invariants.
- Hard: two models to keep in step. If reads lag, users see stale data and must be told.
- Fits: reads and writes differ sharply in shape or load.
- With DDD: a complement. Often the fix when one aggregate is twisted to serve queries. Does not require event sourcing.

**Event sourcing.** *The stored state is the list of events; current state is derived.*
- Easy: audit, history, "what did we know then". Rebuilding read models.
- Hard: events are a published schema forever (SE1/D11). Queries need projections. Deleting personal data.
- Fits: the history is itself a requirement. Ledgers, claims.
- With DDD: a complement to aggregates. It replaces how they are stored. Costly without that requirement.

**Modular monolith.** *One deployable; modules with enforced borders and narrow public interfaces.*
- Easy: one deploy, one transaction, cheap refactoring across modules.
- Hard: borders erode unless a check enforces them (SE1/O3).
- Fits: several contexts, one team or a few. The default before services.
- With DDD: a complement. The natural home for bounded contexts: one module each.

**Microkernel / plugin.** *A small core defines extension points; features arrive as plugins.*
- Easy: third parties extend without touching the core. Optional features.
- Hard: the plugin interface is a one-way door. Interactions between plugins.
- Fits: a product extended by others. Editors, build tools.
- With DDD: orthogonal. The plugin contract is a published language; treat it as one.

### A decision guide

Ask about the problem. Each answer points somewhere.

| Question | If yes |
| -------- | ------ |
| Does anything change state? | No: no aggregates. Functional core, data-driven, or pipes and filters. |
| Is the model yours, or upstream's? | Upstream's: conform. Isolate spelling in one adapter. |
| Do many things differ only in data? | Data-driven. |
| Are there rules experts argue about? | DDD in that subdomain, written as a functional core. |
| Does one word mean two things? | Two contexts. Modular monolith to hold them. |
| More than one surface or source? | Hexagonal ports, but only at those edges. |
| Does change arrive per feature? | Vertical slices. |
| Do reads and writes want different shapes? | CQRS. |
| Is history a requirement? | Event sourcing. |
| Will outsiders extend it? | Microkernel. |
| None of the above? | Transaction script or active record. Stop there. |

Shapes combine. A common honest result: a modular monolith, one context data-driven and
conformist, one context a functional core behind an ACL, ports at the edges only.

### Pairs commonly confused

- **Layered or hexagonal layout, and DDD.** The first two arrange code. DDD decides what the model is.
- **Module and bounded context.** A module groups code. A context is where a language holds.
- **Entity and aggregate.** An entity has identity. An aggregate protects an invariant.
- **Repository and table gateway.** A repository holds aggregates. A gateway reads rows.
- **Adapter and ACL.** An adapter changes spelling. An ACL changes model.
- **CQRS and event sourcing.** Either works without the other.
- **Vertical slice and microservice.** One is a folder. The other is a deployment.
- **Data-driven and "config-driven".** A registry replaces repeated code. A setting never varied is speculation.
- **Hexagonal and clean.** The same dependency rule; clean adds prescribed rings.
