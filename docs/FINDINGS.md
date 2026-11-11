# Findings

What we learned the expensive way: surprises, dead ends, measurements, the attack that worked. The
only document reality writes
([SE1/W10](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w10)).
Blameless: say what would have found it, not who should have asked
([SE1/H12](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/09-people.md#h12)).

The git history, read on 2026-11-11, holds no reverts or hotfixes to learn from. The entries below
come from adopting the guide. The check was first run on 2026-11-11, with data from 5e-database
release 4.5.1 (b207ac7). An entry says whether running confirmed it or it only comes from reading.

## 2026-11-11 — First run of the check: it builds, and 26 of 75 tests fail

- **What happened:** `pixi run check` compiled the project and ended in `BUILD FAILURE`: 75 tests,
  26 failures, 0 errors. 25 are the list test, one per resource; 1 is the proficiency get-by-index
  test. The two entries below explain them. Nothing else failed.
- **Kind:** K2 (surprise)
- **Cost:** until now, no one could tell from the repository whether it worked.
- **Findable by:** CI, which does not exist yet.
- **Changed:** queue items 1 to 4; `CONTRIBUTING.md` names the real commands. With the list test
  corrected and the collection name fixed, the same check passes, 75 of 75. The list test now also
  requires a non-empty list, because an empty one had hidden the wrong collection name.

## 2026-11-11 — The shared list test is out of step with the API (confirmed by running)

- **What happened:** `ControllerTest` asserts that a list response is a JSON array, while every
  controller returns `PagedModel`, an object of `content` and `page`. The list test fails for all 25
  resources, despite commit 5eb107d's message, "Everything is working correct". Its message reads
  backwards, "Expected OBJECT, actual ARRAY", because the assertion's arguments are swapped.
- **Kind:** K3 (we were sure, and wrong)
- **Cost:** a suite that cannot go green hides any new failure among the old ones.
- **Findable by:** running the tests on every push.
- **Changed:** queue item 3.

## 2026-11-11 — One collection name has its prefix twice (confirmed by running)

- **What happened:** `ProficiencyDocument` names `2014-2014-proficiencies`; the other 24 have one
  `2014-` prefix, and upstream loads `2014-proficiencies`. Commit 86907fb changed these names in bulk.
  Getting `heavy-armor` returned no item with an `index`.
- **Kind:** K2 (surprise)
- **Cost:** `/api/proficiencies` and its MCP tools serve no data.
- **Findable by:** the existing proficiency test, run against seeded data.
- **Changed:** queue item 4.

## 2026-11-11 — A recording was only stable against the data it was recorded from

- **What happened:** the first golden master matched itself, then 176 responses differed after a
  reseed. `mongoimport` does not keep insertion order by default, and an unsorted list follows the
  stored order. Seeding with `--maintainInsertionOrder` fixed it; the record then survived a reseed
  and a server restart unchanged.
- **Kind:** K3 (we were sure, and wrong)
- **Cost:** one re-recording. Any store that replaces MongoDB must keep upstream's file order.
- **Findable by:** comparing across a reseed, not only across two runs.
- **Changed:** `scripts/seed.py`.

## 2026-11-11 — The uniform template has quiet exceptions (confirmed by running)

- **What happened:** of the 25 MCP list tools, 23 take `pageNumber` and `pageSize` with no filter, and
  2 take a page object and a filter. The 23 default `pageNumber` to 1 on a zero-based API, so a call
  with no arguments skips the first page.
- **Kind:** K2 (surprise)
- **Cost:** an agent gets a different interface by resource, and misses the first page by default.
- **Findable by:** one test that compares the tools' shapes.
- **Changed:** queue item 11.

## 2026-11-11 — Links and errors that do not behave (confirmed by running)

- **What happened:** every `url` in a response is upstream's, `/api/2014/...`, and this API answers
  those paths with HTTP 500. An unknown route returns 500, not 404, and so does a non-numeric value
  for the `level` filter on `/api/levels`. `GET /api/equipment?query=` ignores `query`.
- **Kind:** K2 (surprise)
- **Cost:** a client cannot follow a link the API gives it.
- **Findable by:** following one returned `url` in a test.
- **Changed:** queue items 7 and 12.

## 2026-11-11 — GraphQL is advertised and has no implementation (confirmed by running)

- **What happened:** the README and `schema.graphqls` offer GraphQL queries, but a search of the
  source found no `@QueryMapping`, `@SchemaMapping`, or data fetcher. `getAlignments` answers HTTP 200
  with `{"data":{}}`.
- **Kind:** K2 (surprise)
- **Cost:** a client relying on the README gets nothing useful from those queries.
- **Findable by:** one test that issues a GraphQL query and asserts on the data.
- **Changed:** queue item 5.

## 2026-11-11 — Every fresh agent tripped on the same three things (confirmed by running)

- **What happened:** three Claude Haiku agents, one per refactor branch, fixed the same defect. All
  three looked for the *Working* checklist that `CONTRIBUTING.md` names as the definition of done,
  and did not find it: it is in the guide's `CHECKLISTS.md`, which is not vendored. All three tried
  to stop the server with `pkill -f` and killed their own shell, because the pattern matched it; the
  session that built the branches had done the same. And all three saw the OpenAPI document's hash
  differ before they changed anything: springdoc's output is not byte-stable between runs.
- **Kind:** K2 (surprise)
- **Cost:** each agent spent calls on each one, and one left a server running that it could not stop.
  The `schema` measure cannot be trusted as recorded.
- **Findable by:** handing the repository to a fresh agent, which is what found it.
- **Changed:** nothing yet. Proposed: vendor `CHECKLISTS.md` with the guide or copy the *Working*
  list into `CONTRIBUTING.md`; add a `pixi run stop` that stops the server by its recorded process ID;
  and make the golden script compare the OpenAPI document in a canonical order, or drop the measure.

