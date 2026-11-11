# Experiment: is the layering essential or imposed?

The code's structure might be essential to managing its complexity
([SE1/D8](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/05-design.md#d8)), or it
might be imposed. Two branches take opposite answers, from the same base, against the same record of
behaviour. The aim is to see whether there is a difference, not to prove a side.

## The two shapes

Both forks differ in DDD's own terms: whether this project's model may differ from upstream's.

| Branch | Stance toward 5e-database | Shape |
| ------ | ------------------------- | ----- |
| `refactor/ddd` | Anticorruption Layer | Our model is ours, and upstream is translated at the border. Bounded contexts, value objects with invariants, and no framework types in the domain. |
| `refactor/projection` | Conformist | The API is a projection of upstream data, and a resource is a row in a table, not a package. One registry, one pure transform, one store, and surfaces generated from the registry. Types only where a rule or an exception earns one. |

`refactor/ddd` should be the version of the argument its best advocate would defend. Its shape sentence
goes to Jackson Hill for review before it is built.

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

## Prediction, written before either branch exists

`refactor/projection` wins changes 1, 2, 3, and 5 clearly, and `refactor/ddd` wins change 4. The
number to watch is how much each wins by, and whether projection can add the rule locally without
growing a domain layer anyway. If it cannot, the layering was essential early.

## Progress on `refactor/projection`

Kept current before each step, so a cut session can be resumed from here.

- 2026-11-11 — started. `REQUIREMENTS_v2.md` written (shape only). The transform's rules were found
  by diffing upstream's JSON against the recorded bodies, not by reading the 123 classes. 17 of 25
  resources are upstream's documents with camelCase keys and no nulls. The other 8 depart from
  upstream in about 40 ways, nearly all of them lost data. MCP output equals REST output plus a
  `null` for each field the old types declared and an item lacks.
- Known before starting: 7 recorded cases (1 REST, 6 MCP) have error text that names the old Kotlin
  classes, such as `AbilityScoreTools.getAbilityScores`. Any change of shape changes that text, on
  either branch. Their status codes can still match.
- 2026-11-11 — refactor done and committed. `pixi run check`: 75 of 75, `BUILD SUCCESS`, with no
  test changed. `pixi run golden`: 2602 of 2603 `rest` and 2465 of 2471 `mcp` match. The 7 that
  differ are exactly the known ones, each with the same status. `schema` differs: the document keeps
  all 51 paths, their tags, summaries, and parameters, but has no response schemas. Removing one
  departure (`Decimal("weight")`) made 445 cases differ, so the departures carry weight.
- Kotlin: 6,756 lines in 300 files on `refactor/base`; 771 lines in 7 files here, 337 of them the
  registry's rows.
- 2026-11-11 — the owner amended rule 3, so the 7 class-naming cases are held by status only.
  After merging that from `refactor/base`, this branch prints `GOLDEN MATCH`.
- 2026-11-11 — change 1 done: one registry row, `pixi run check` 78 of 78, golden re-recorded with
  33 new cases and `GOLDEN MATCH`. The work of it is in `CHANGE_WORK.md`.
- **Torn:** nothing. Change 2 has not started.
