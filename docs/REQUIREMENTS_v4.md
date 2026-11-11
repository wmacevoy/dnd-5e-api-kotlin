# Requirements — v4 (branch `refactor/layered`)

What must be observably true, and what is not being built. Changes only with the client; a change in
meaning is a new version (`REQUIREMENTS_v5.md`); a spelling or lint fix is not. See
[SE1/R1–R11](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md).

**What changed from v3:** SHAPE-1 and SHAPE-3 are corrected. v2 and v3 called the subject packages
bounded contexts, and the persistence adapter an anticorruption layer. Neither is true of this code:
the packages share one vocabulary and one model, and the output is held to upstream's shape. The
branch was renamed from `refactor/ddd` for the same reason (`EXPERIMENT.md`).

**What changed from v2 to v3:** change 1 of `EXPERIMENT.md` adds a 26th resource, the 2024 weapon mastery
properties, so API-1, MCP-1, DAT-1, and the glossary's count of resources are new in v3.

**What changed from v1 to v2:** only *Fundamental shape*, for the experiment (its rule 2). SHAPE-1 is
replaced, SHAPE-3 is new, and SHAPE-2 is unchanged. Each line says the version that last changed it,
and is cited by that version. Nobody who can decide has accepted this shape. It is
one side of an experiment, and Jackson Hill has not yet reviewed this statement of his position.

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
| Resource | One kind of SRD entity with its own endpoint, such as spells or monsters. There are 26. |
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

- **SHAPE-1** *(since v4)* *(judged)* *(inferred)* The code is grouped into six packages by subject:
  character, progression, spellcasting, equipment, bestiary, and rules. They help a reader find
  things. They are not bounded contexts, because they share one vocabulary and one shared model.
  Within a package, every resource is built the same way, in three layers. The domain holds the
  model and its repository port, with no storage fields. The application holds one query service per
  resource and the views clients see. The infrastructure holds the delivery adapters and the
  persistence adapter. A layer depends only on the layers below it. Judge: the holder, once named.
- **SHAPE-2** *(since v1)* *(judged)* *(inferred)* The API is a read-only window on data another
  project owns. Judge: the holder, once named.
- **SHAPE-3** *(since v4)* *(judged)* *(inferred)* Each resource's shape is written three times:
  upstream's document, the model, and the view clients see, with hand-written mappers between them.
  The persistence adapter translates upstream's spelling into the model, so an upstream rename stops
  there. The model does not yet differ from upstream in meaning, because the output is held to
  today's. A rule the model relies on is an invariant of a model type. Judge: the holder, once named.
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

- **API-1** *(since v3)* *(inferred)* The REST API serves these 26 resources under `/api`:
  `ability-scores`, `alignments`, `backgrounds`, `classes`, `conditions`, `damage-types`,
  `equipment`, `equipment-categories`, `feats`, `features`, `languages`, `levels`, `magic-items`,
  `magic-schools`, `monsters`, `proficiencies`, `races`, `rules`, `rule-sections`, `skills`,
  `spells`, `subclasses`, `subraces`, `traits`, `weapon-properties`, and, from the 2024 SRD,
  `weapon-mastery-properties`.
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
- **MCP-1** *(since v3)* *(inferred)* The server exposes 52 MCP tools, one list and one get-by-index
  per resource, returning the same data as REST. Its SSE message endpoint is `/mcp/messages`.
- **GQL-1** *(since v1)* *(inferred)* `/graphql` answers `getAbilityScores`,
  `getAbilityScoreByIndex`, `getAlignments`, and `getAlignmentByIndex`, and `/graphiql` serves an
  explorer. The README promises this and the schema declares it, but no code behind it was found
  (queue item 5).
- **DAT-1** *(since v3)* *(inferred)* Data comes from the `2014-*` collections of the MongoDB database
  `5e-database`, and from its `2024-weapon-mastery-properties`, using snake_case field names.

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
