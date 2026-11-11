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
   branch, so that neither gains from fixing one.
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

- Files touched.
- Diff lines a reviewer must read, not counting generated files.
- Whether a fresh agent, given only the repository and the change's text, finishes it, and how far it gets.
- Whether `pixi run check` and `pixi run golden` pass afterwards.

Also recorded once per branch: lines of Kotlin, and the time from a cold start to explaining how one
request reaches the data.

## Prediction, written before either branch exists

`refactor/projection` wins changes 1, 2, 3, and 5 clearly, and `refactor/ddd` wins change 4. The
number to watch is how much each wins by, and whether projection can add the rule locally without
growing a domain layer anyway. If it cannot, the layering was essential early.
