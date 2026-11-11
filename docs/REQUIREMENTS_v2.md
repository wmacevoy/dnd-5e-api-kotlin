# Requirements — v2 (branch `refactor/ddd`)

What must be observably true, and what is not being built. Changes only with the client; a change in
meaning is a new version (`REQUIREMENTS_v3.md`); a spelling or lint fix is not. See
[SE1/R1–R11](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md).

**What changed from v1:** only *Fundamental shape*, for the experiment in `EXPERIMENT.md` (its rule
2). It is a domain-driven shape stated by purpose, following `PROPOSAL_DDD_SHAPE.md`, in six items
that would commit any project to the approach. Every other line is v1's, so everything else is still
cited as `REQ1/...`. Nobody who can decide has accepted this shape. It is one side of an experiment.

**Rebuilt on 2026-11-11 from the code, the README, and the git history
([SE1/W12](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w12)).**
Nobody who can decide has confirmed any of it. Every requirement states what the code was read to do,
and the code was not run. *Tell us what is wrong with it.*

Client (proposed, *(inferred)*): the repository owner, as proxy for the API's users, with the issue
tracker as the read-back
([SE1/H5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/09-people.md#h5)). Not
confirmed. Date sent: not yet sent.

## Glossary

| Term | Meaning |
| ---- | ------- |
| SRD | The D&D 5th Edition System Reference Document, the source of all data here. |
| Resource | One kind of SRD entity with its own endpoint, such as spells or monsters. There are 25. |
| Index | A resource item's stable, URL-safe identifier, such as `neutral` or `heavy-armor`. |
| Paged list | A response of `content` (the items) and `page` (page, size, sort). |
| Example | The optional filter fields on a list request, matched against stored items. |
| View | The shape of an item as returned to a client. |
| Tool | An operation exposed to an AI agent over MCP. |
| 5e-database | The upstream project whose data populates MongoDB. |

## Mission

- **MISSION-1** *(since v1)* *(judged)* *(inferred)* Give developers of character builders, virtual
  tabletops, and AI applications programmatic access to the D&D 5e SRD. Judge: not named. Veto: not
  stated. Source: the README.

## Fundamental shape

Held by: not named.

- **SHAPE-1** *(since v2)* *(judged)* *(inferred)* **Purpose decides where effort goes.** There is no
  core subdomain yet. Serving SRD entries, the Compendium, is a supporting subdomain: it is kept
  plain and conforms to upstream. Character Rules, which answers questions the data does not state
  outright, becomes the core when its first rule arrives (`EXPERIMENT.md`, change 4). Paging,
  storage, HTTP, and MCP are generic, and come from libraries. Judge: the holder, once named.
- **SHAPE-2** *(since v1)* *(judged)* *(inferred)* The API is a read-only window on data another
  project owns. Judge: the holder, once named.
- **SHAPE-3** *(since v2)* *(judged)* *(inferred)* **One language per context.** The Compendium's
  words are *entry* (one SRD item), *kind* (spells, monsters, and the rest), *index* (an entry's
  identity within its kind), and *filter*; each kind's own noun comes from the SRD. The code uses
  these words, and no word is defined twice with different meanings. Judge: the holder, once named.
- **SHAPE-4** *(since v2)* *(judged)* *(inferred)* **Every border is a named relationship.** 5e-database
  to the Compendium is conformist: an entry is upstream's, and upstream's spelling (collection names,
  snake_case keys, storage fields) appears in one adapter only. The Compendium to its clients is an
  open host service: today's output, departures from upstream included, is the published language,
  written in one place. The Compendium to Character Rules will be an anticorruption layer, owned by
  Character Rules. Judge: the holder, once named.
- **SHAPE-5** *(since v2)* *(judged)* *(inferred)* **Rules live in a model; state has one owner.**
  Upstream owns all state, the store is a copy, and nothing here writes; so there are no aggregates,
  and none are pretended. A rule, when one arrives, is a pure function over value objects that hold
  their own invariants, in Character Rules. Judge: the holder, once named.
- **SHAPE-6** *(since v2)* *(judged)* *(inferred)* **The model depends on nothing outside it.** The
  Compendium's model imports no framework, storage, or delivery type. It reaches storage through one
  port it defines, and REST and MCP are adapters over one query service, so the same question gets
  the same answer on each. Judge: the holder, once named, from the imports.
- **Says no to:** see `Non-goals`.
- **Exceptions:** equipment has a search endpoint that no other resource has (`REQ1/API-5`).

## The pain

Not known. Nobody has said what goes wrong today or who gets the call.

## People this touches

*(inferred)* Developers calling the API; AI agents and the people using them through MCP; players
and game masters using whatever those developers build; the maintainers of this repository; and
5e-database's maintainers, whose schema this depends on.

## Requirements

Every line below is *(inferred)*. Tests do not name these IDs yet (queue item 9).

- **API-1** *(since v1)* *(inferred)* The REST API serves these 25 resources under `/api`:
  `ability-scores`, `alignments`, `backgrounds`, `classes`, `conditions`, `damage-types`,
  `equipment`, `equipment-categories`, `feats`, `features`, `languages`, `levels`, `magic-items`,
  `magic-schools`, `monsters`, `proficiencies`, `races`, `rules`, `rule-sections`, `skills`,
  `spells`, `subclasses`, `subraces`, `traits`, `weapon-properties`.
- **API-2** *(since v1)* *(inferred)* `GET /api/{resource}` returns a paged list and accepts `page`,
  `size`, and `sort`.
- **API-3** *(since v1)* *(inferred)* `GET /api/{resource}/{index}` returns the item with that index,
  including its `index` field.
- **API-4** *(since v1)* *(inferred)* A request for an index that does not exist returns HTTP 404.
- **API-5** *(since v1)* *(inferred)* `GET /api/equipment/search?query=` returns a paged list of
  equipment whose name contains `query`, ignoring case, sorted by name by default.
- **API-6** *(since v1)* *(inferred)* List endpoints accept the resource's filter fields as query
  parameters (24 of the 25 controllers declare them).
- **API-7** *(since v1)* *(inferred)* Null fields are left out of JSON responses.
- **DOC-1** *(since v1)* *(inferred)* Swagger UI at `/swagger-ui/index.html` documents the REST API.
- **MCP-1** *(since v1)* *(inferred)* The server exposes 50 MCP tools, one list and one get-by-index
  per resource, returning the same data as REST. Its SSE message endpoint is `/mcp/messages`.
- **GQL-1** *(since v1)* *(inferred)* `/graphql` answers `getAbilityScores`,
  `getAbilityScoreByIndex`, `getAlignments`, and `getAlignmentByIndex`, and `/graphiql` serves an
  explorer. The README promises this and the schema declares it, but no code behind it was found
  (queue item 5).
- **DAT-1** *(since v1)* *(inferred)* Data comes from the `2014-*` collections of the MongoDB database
  `5e-database`, using snake_case field names.

## Inputs: absent, late, wrong, lying

All *(inferred)* from reading the code. A blank-looking answer is a real gap.

| Input | Absent | Late | Wrong | Lying |
| ----- | ------ | ---- | ----- | ----- |
| Path `index` | Becomes the list endpoint | Not applicable | 404 | Not applicable; the API is public and read-only |
| Paging parameters | Spring defaults; equipment sets size 10 and sort by name | Not applicable | Not examined | Not examined |
| Filter fields | Unfiltered list | Not applicable | Empty list | Not examined |
| MongoDB | Requests fail; a data-access exception would map to HTTP 400 | Not specified; driver timeouts apply | A renamed collection reads as empty | The API returns whatever is stored |

## Operating envelope

Nothing here has been measured or agreed. The README says "high-performance", with no number behind
it, and that phrase is not a requirement until it has one.

- **Scale:** not stated.
- **Speed:** not stated.
- **Recovery:** not stated. The data can be rebuilt from 5e-database.
- **Availability:** not stated.

## Security, law, and ethics

*(inferred)* The API has no authentication and configures no CORS policy. It collects no data about
people. The SRD's license and the attribution it requires have not been checked (see `Not yet
answered`).

## Assumptions (K4s), read back to the client

Not yet read back.

- "We're assuming the API is public and needs no authentication. True?" — not answered
- "We're assuming only the 2014 ruleset is served. True?" — not answered
- "We're assuming the client for the equipment search lives in another repository. True?" — not answered

## Scope decisions

- Serve the 2014 SRD collections — *(inferred)*: commit 86907fb moved every collection to `2014-*`;
  the reason was not recorded.

## Non-goals

All *(inferred)* from what the code does not do.

- Not building create, update, or delete endpoints — the data belongs to 5e-database. The repositories
  have a `save` method, but no use case calls it.
- Not building user accounts or authentication — nothing in the code or dependencies provides them.
- Not building a user interface in this repository — it has none, though the latest commit message
  mentions an "Equipment Page".

## Not yet answered

- Who is the client, and who holds the fundamental shape? — the repository owner
- Is this repository a fork that follows its original author, or does it now go its own way? Most
  commits are by another author. — the repository owner
- Should GraphQL be built out or dropped? — the client (queue item 5)
- What license and attribution does serving the SRD require? — the repository owner
- Which guide release should the *Foundation* line pin? — the holder of the shape

## Deferred

None agreed.
