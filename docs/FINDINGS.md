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

## 2026-11-11 — GraphQL is advertised and has no implementation (from reading only)

- **What happened:** the README and `schema.graphqls` offer GraphQL queries, but a search of the
  source found no `@QueryMapping`, `@SchemaMapping`, or data fetcher.
- **Kind:** K2 (surprise)
- **Cost:** a client relying on the README gets nothing useful from those queries.
- **Findable by:** one test that issues a GraphQL query and asserts on the data.
- **Changed:** queue item 5.
