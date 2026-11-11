# Experiment: is the layering essential or imposed?

The code's structure might be essential to managing its complexity
([SE1/D8](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/05-design.md#d8)), or it
might be imposed. Two branches take opposite answers, from the same base, against the same record of
behaviour. The aim is to see whether there is a difference, not to prove a side.

## The three shapes

The question is Jackson Hill's: is his layering essential? One branch keeps it, one does not, and
one starts from domain-driven design's purpose rather than its layout.

| Branch | Stance toward 5e-database | Shape |
| ------ | ------------------------- | ----- |
| `refactor/layered` | Our model is ours, in name: three copies of each resource's shape (document, model, view), with mappers between them | Jackson's layering kept and tightened: packages grouped by subject, three layers in each, one query service per resource, and `Index` as a value object. |
| `refactor/projection` | Conformist | The API is a projection of upstream data, and a resource is a row in a table, not a package. One registry, one pure transform, one store, and surfaces generated from the registry. Types only where a rule or an exception earns one. |
| `refactor/ddd` | Conformist, with upstream's spelling in one adapter; an anticorruption layer only in front of a rules context, from change 4 | Domain-driven by purpose (`PROPOSAL_DDD_SHAPE.md`). The Compendium context's model and ports import no framework. Each fact lives with its owner: kinds and their filters in the model; upstream's spelling in the storage adapter; today's output, with its departures, as the published language; each surface's names in that surface. |

`refactor/layered` should be the version of Jackson's argument its best advocate would defend. Its
shape sentence goes to Jackson Hill for review. It was built before he saw it, at the owner's word.

**Renamed on 2026-11-11.** The branch was `refactor/ddd`, but it did not test domain-driven design.
Its shape described packages and layers, not purpose. `PROPOSAL_DDD_SHAPE.md`, written
independently, argues that DDD done by purpose would conform to upstream for serving entries, close
to `refactor/projection`. It would place its one anticorruption layer between the entries and a
rules context, which arrives with change 4. That third position is now `refactor/ddd`, built
second, on 2026-11-11, from the same base. Change 4 on `refactor/projection` follows it too.

## Rules

1. Both branches fork from `refactor/base`.
2. Each branch gets a `REQUIREMENTS_v2` that differs from v1 only in *Fundamental shape*
   ([SE1/R5](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/04-requirements.md#r5)).
3. Before any change below, a branch passes `pixi run golden` with no `rest` or `mcp` difference.
   It preserves today's behaviour, defects included. Defects in the queue are not fixed on either
   branch, so that neither gains from fixing one. One exception, decided by the owner on
   2026-11-11: where a recorded error's text names a class under `com.thejaxonhill`, only its
   status is held. That text describes the old code, not behaviour a client can rely on, and no
   change of shape can keep it. `golden/manifest.tsv` marks these 7 cases `unchecked` in place of
   a hash.
4. The `schema` group (OpenAPI) is measured and reported, and cannot fail a branch.
5. GraphQL stays out of the refactor itself. It is change 3.

## The changes, applied to both, in order

Each is one commit or a short series, starting from that branch's refactor.

1. **A 26th resource.** Serve the 2024 `weapon-mastery-properties` (8 items) over REST and MCP, the
   same way as the other 25.
2. **An upstream rename.** Upstream renames the field `desc` on spells to `description`, and the
   collection prefix `2014-` to `srd2014-`. The API's output must not change.
3. **A new delivery surface.** GraphQL answers list and get-by-index for all 25 resources, with the
   same data as REST.
4. **A real rule.** Given six ability scores, say whether a character meets a class's multiclassing
   prerequisites, and which are unmet. Upstream states these as `prerequisites` (all required) or
   `prerequisite_options` (a choice), so the rule has a real branch in it.
5. **Replace MongoDB.** Serve from the upstream JSON held in memory, with no database at runtime.
   `pixi run golden` must still match. This one is the classic argument for ports and adapters, so
   it is the layering's best chance. Bringing the JSON into the repository needs the SRD's licence
   checked first, unless it is fetched at build time.

## What is measured, per change

The design pressure under test is ease of change: less how much code a change takes than how
simple that code is to think about. So the first measure is the work of thinking, and the counts
come after it.

- The work of making it, recorded in `CHANGE_WORK.md` as it happens: what had to be known, read,
  held in mind at once, and decided, and which edits the change needed versus which the shape
  needed.
- Files touched.
- Diff lines a reviewer must read, not counting generated files.
- **The fresh-agent test.** A smaller model, Claude Haiku, is given only the branch at that commit, in
  an isolated worktree, and one task's text: add a feature or fix a defect. It gets no hints and no
  help. Record whether it finishes, whether `pixi run check` and `pixi run golden` pass, which files
  it touched, its wrong turns, and how much it read before its first edit. Then score its transcript
  against the items in `CHANGE_WORK.md`. The same task, worded identically, goes to both branches.
  The agent that built the shapes cannot take this test: its entries are a floor, and this one is
  the cold start. Tasks: each change below, and fixes drawn from `FINDINGS.md`, such as the levels
  that lose their spell slots. A fix must change only the golden cases it names. Re-record them, and
  every other hash must stay the same.
- Whether `pixi run check` and `pixi run golden` pass afterwards.

Also recorded once per branch: lines of Kotlin, and the time from a cold start to explaining how one
request reaches the data.

## Fresh-agent test 1: the spell-slot fix, 2026-11-11

Task, identical on each branch: make every level's spell slots appear, named as the API names other
fields; meet the repository's definition of done; re-record the golden master and show that only
cases the fix explains changed. Each agent was Claude Haiku in its own worktree, with no hints.
Results below were checked by rebuilding, running `pixi run check` and `pixi run golden` in each
worktree, and diffing the three new manifests, not taken from the agents' reports.

| | `refactor/projection` | `refactor/layered` | `refactor/ddd` |
| --- | --- | --- | --- |
| Finished | yes | yes | yes |
| Code changed | 1 file: 2 lines removed from the registry | 2 files, 33 lines: a new document type with nine `@Field` names, and its mapper | 1 file: 2 lines removed from the published language |
| Test | 1, seen red first | 1, seen red first | 1, seen red first |
| `pixi run check` | 79 of 79 | 82 of 82 | 82 of 82 |
| Golden | `GOLDEN MATCH`; 326 level cases re-recorded, no others | the same | the same |
| Tool calls, tokens, minutes | 31, 132k, 5.2 | 45, 143k, 8.4 | 34, 132k, 5.8 |
| Requirement cited | a shape item of its branch's requirements v2, a poor fit | `REQ3/DAT-1` | `REQ1/API-3` |

- **The three fixed APIs are identical:** their re-recorded manifests match for all 5,107 `rest` and
  `mcp` cases.
- **All three fixed only what was asked,** and reported that the magical secrets and mystic arcanum
  fields are lost by the same cause, as `AGENTS.md` says to.
- **Judgment differed.** ddd's agent reproduced the defect and read upstream's document before
  editing, and raised that rule 3 makes changed output a decision for clients. Layered's agent kept
  storage names out of the domain type, at the cost of a new type. Projection's and ddd's agents
  deleted the comment that still explains the fields they left dropped.
- **Shared friction, on every branch:** the definition of done points to a checklist that is not in
  the repository; there is no command to stop the server, and all three agents killed their own
  shell trying to (as the session that built the branches had); and the OpenAPI hash is not stable
  between runs, so each agent saw a `schema` difference that its fix did not cause. See `FINDINGS.md`.
- **Left behind:** layered's agent could not stop its server on port 18082; a permission refusal
  stopped it, and it was not stopped for it. One of its searches reached outside its worktree.

## Prediction, written before either branch exists

`refactor/projection` wins changes 1, 2, 3, and 5 clearly, and `refactor/layered` (then named
`refactor/ddd`) wins change 4. The
number to watch is how much each wins by, and whether projection can add the rule locally without
growing a domain layer anyway. If it cannot, the layering was essential early.

## Progress on `refactor/ddd`

Kept current before each step, so a cut session can be resumed from here.

- 2026-11-11 — started from `refactor/base`, at the owner's word ("one more try"), after
  `refactor/layered` was found to test layering, not DDD. `REQUIREMENTS_v2.md` written (shape only).
  The Compendium's code will resemble projection's, because the shape conforms to upstream. What
  differs is where each fact lives.
- 2026-11-11 — refactor done and committed. `pixi run check`: 78 of 78 (75 old tests unchanged, 3 new
  for the Compendium's values, seen failing against broken rules). `pixi run golden`: `GOLDEN MATCH`
  on the first run; `schema` differs as on projection, with no response schemas.
- Kotlin: 6,756 lines in 300 files on `refactor/base`; 806 lines in 11 files here. The model
  (`compendium/`, 118 lines) imports nothing.
- 2026-11-11 — change 1 done: one `KINDS` row and an `edition` on `Kind`, `pixi run check` 81 of 81,
  `GOLDEN MATCH`. Its manifest equals both other branches' for every `rest` and `mcp` case. The work
  of it is in `CHANGE_WORK.md`.
- **Torn:** nothing. Change 2 has not started.
