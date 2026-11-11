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

## Prediction, written before either branch exists

`refactor/projection` wins changes 1, 2, 3, and 5 clearly, and `refactor/layered` (then named
`refactor/ddd`) wins change 4. The
number to watch is how much each wins by, and whether projection can add the rule locally without
growing a domain layer anyway. If it cannot, the layering was essential early.
