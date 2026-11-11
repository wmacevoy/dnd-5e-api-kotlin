# The work of making a change

A structure earns its keep by making change easy. That is not the same as making it short. The
question here is whether a structured approach makes the code for a change simple to think about.
That covers knowing where to go, what must be true there, and when you are done
([SE1/D8](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/05-design.md#d8),
[SE1/D9](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/05-design.md#d9)).
Lines and files are easy to count and easy to argue about. The thinking is what costs, and it
leaves no trace unless someone writes it down while it happens.

This file records that thinking, one entry per change, per branch, for the experiment in
`EXPERIMENT.md`. Each branch adds its entries below its own heading. The two branches' entries for the
same change are meant to be read side by side.

## How to keep an entry honest

- **Write it as you go, not afterwards.** Memory tidies a path that was not tidy. Write each question
  when you ask it, and its answer when you find it.
- **Record wrong turns.** A wrong turn caught late is the most expensive thing a structure can
  allow, so it is data, not embarrassment.
- **Say who did the work.** An author who already knows the code is not a cold start. Its entry
  measures the floor, and a fresh agent or newcomer measures the real cost.
- **Count only what the change needed.** Write down anything you fixed or tidied along the way, but
  leave it out of the counts.

## What an entry records

1. **The change**, as given, and who made it, with how much prior knowledge of the code.
2. **Questions before code.** What had to be known before writing anything. For each question:
   where the answer was (a file and lines, upstream, a document, or nowhere), and whether the
   structure pointed there or it had to be searched for.
3. **Held at once.** The facts that had to be in mind at the same moment to get the change right:
   the working set. Each one is a chance to forget something. The count matters, and so does which
   of them the code would have caught if forgotten.
4. **Decisions.** The choices the structure left open. Mark each one *mine* (an implementation
   choice) or *the owner's* (vocabulary, interface, or a requirement). Say which way each went.
5. **Edits, each tagged.** Every place touched and why: *essential* (any shape would need it, because
   the change asks for it), *shape* (this structure requires it), or *incidental* (tooling or data
   outside the code).
6. **Wrong turns,** and what caught each one: the compiler, a test, the golden master, review, or
   nothing yet.
7. **Done.** How the author knew it was finished, and what would have said so if it was not.
8. **The structure's part.** One sentence on what the shape did for this change, and one on what it
   did against it.
9. **Counts.** Files touched, the diff lines a reviewer reads, whether `pixi run check` and `pixi run
   golden` pass, and the time taken if it was measured.

## refactor/projection

### Change 1 — a 26th resource, the 2024 weapon mastery properties

1. **The change and who made it.** As written in `EXPERIMENT.md`. Made on 2026-11-11 by Claude Opus
   5.5, the agent that wrote this branch's shape that same day. So this entry is the floor, not a
   cold start: nothing had to be found in the code.
2. **Questions before code.**
   - *What is the data?* Upstream's `src/2024/5e-SRD-Weapon-Mastery-Properties.json`: 8 items of
     `index`, `name`, `description`, `url`. Found by listing upstream. Nothing in this repository
     points there.
   - *Is it in the database?* No. `scripts/seed.py` loads only `src/2014`.
   - *What is the collection called?* `2024-weapon-mastery-properties`, by upstream's rule in its
     `scripts/dbUtils.ts`: the prefix is the year's directory.
   - *Which requirements change?* API-1 (the list of resources), MCP-1 (the count of tools), DAT-1
     (the collections), and the glossary's count. Found by reading the requirements. The code does
     not point there.
   - *How is a resource added?* One `Resource` row. Its KDoc names every field, and the existing 25
     rows are examples.
3. **Held at once.** Five things. The row's seven positional values (path, collection, tag, plural,
   singular, list tool, get tool). That the list tool takes the 23-tool argument shape by default.
   That a new resource has no departures and no MCP nulls, because no old types ever declared its
   fields. That the seed and `golden.py` each keep their own list of resources. That a requirement
   whose meaning changes makes every citation of it stale. If forgotten: the citations are caught
   by `./se --doctor`, and a missing seed is caught by the new test. A wrong tool name or noun is
   caught by nothing.
4. **Decisions.**
   - *Mine:* the MCP list tool takes `pageNumber` and `pageSize` like 23 of the 25, defect included
     (queue item 11), because the change says "the same way as the other 25". Load only this one
     2024 file rather than all of 2024. Sort `golden.py`'s filter cases so a re-record diffs cleanly
     (incidental, see 5).
   - *The owner's, made here and open to reversal:* the path has no year in it,
     `/api/weapon-mastery-properties`, while the items' own `url`s say `/api/2024/...`. The tool names
     `getWeaponMasteryProperties` and `getWeaponMasteryProperty`. The noun "weapon mastery property"
     enters the vocabulary. The change's text implied all three, but none was asked.
5. **Edits, each tagged.**
   - *Essential:* the registry row (6 lines). A test naming `REQ3/API-1` (6 lines).
     `REQUIREMENTS_v3` (21 diff lines against v2).
   - *Shape:* none. No other code had to change.
   - *Incidental:* `seed.py` loads the 2024 file (13 lines). `golden.py` gains a row (1 line). The
     manifest is re-recorded (generated: 33 new cases, plus new hashes for `tools/list` and the
     schema). Three code comments, one test comment, `DESIGN_v2`, `QUEUE`, and `CONTRIBUTING` move
     their citations to v3 or correct a count (25 lines in all): the cost of versioned requirements,
     the same in any shape. Not counted: sorting `golden.py`'s filter cases (1 line), a fix found
     on the way.
6. **Wrong turns.** One. A citation in `RestApi.kt` was missed by a pattern that expected text the
   previous step had not yet written. `./se --doctor` would have flagged it. A re-read of the diff
   caught it first.
7. **Done.** The new test, seen failing 3 of 3 before the row, passes. `pixi run check`: 78 of 78.
   Before the re-record, `pixi run golden` differed only in `tools/list` (50 tools became 52). After
   it, every earlier case keeps its hash except `tools/list` and the schema, and the compare prints
   `GOLDEN MATCH`.
8. **The structure's part.** *For:* the change touched one place in the code, and the 25 rows above
   it answered every question about form. *Against:* the row's positional strings are easy to
   misorder, and nothing checks that tool names follow the pattern. Making it right is a matter of
   care, not structure.
9. **Counts.** 14 files, counting the generated manifest. Lines a reviewer reads: 33 essential,
   14 incidental for data and tooling, 25 incidental for citations, and 0 for the shape. Kotlin
   changed in 1 file for behaviour (6 lines) and in 3 files for comments. `pixi run check`: 78 of 78.
   `pixi run golden`: `GOLDEN MATCH` on 5,108 cases. Time: a few minutes of wall clock, most of it
   builds and the golden master; an agent's clock says little about a person's.

## refactor/ddd

No entries yet.
