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

No entries yet.

## refactor/layered

### Change 1 — a 26th resource, the 2024 weapon mastery properties

1. **The change and who made it.** As written in `EXPERIMENT.md`. Made on 2026-11-11 by Claude Opus
   5.5, which wrote this branch's refactor that day and had already made change 1 on
   `refactor/projection`. So every question outside the code was already answered (where the data
   is, upstream's collection name, which requirements change), and the shared edits were applied from
   projection's. This entry measures what the shape adds, and it is a floor.
2. **Questions before code.**
   - *Which context does it belong to?* `equipment`, beside weapon properties. The shape asks this
     and projection's does not. `DESIGN_v2.md` lists the contexts, and the answer took judgment, not
     search.
   - *Which existing resource is nearest?* Weapon properties, with the same four kinds of field.
     Copying the nearest neighbour is how this shape grows, and choosing the neighbour is the
     first decision.
   - *What differs from the neighbour?* One field. Upstream's 2024 item has a `description` string
     where a 2014 weapon property has a `desc` list. Found by reading the data.
   - *Where are resources registered?* In `DeliveryConfig`, which lists every MCP tool object by
     hand. Known from the refactor. A newcomer would find it only by noticing the MCP tools missing.
3. **Held at once.** Seven things. The nine files a resource needs, one per role across three layers.
   Eight forms of the name: `WeaponMasteryProperty`, `WeaponMasteryProperties`,
   `weaponMasteryProperty`, "weapon mastery properties", "a weapon mastery property", "Weapon
   Mastery Properties", the path, and the collection, whose prefix is now a different year. The
   field change in five places across four layers: domain, view, document, and the two mappers.
   That `!!` in the mapper makes `description` required. The registration in `DeliveryConfig`. The
   MCP list tool's argument shape, which the copy brought along without anyone choosing it. The
   shared edits, already done. If forgotten: the compiler catches a missed rename or field, because
   the types connect the layers. A missing `DeliveryConfig` entry is caught by the golden master's
   `tools/list`, not by the tests, because the REST tests pass without it.
4. **Decisions.**
   - *Mine:* copy weapon properties. Make `description` required, as the neighbour's `desc` is, so a
     document without one fails as a 500 where projection's shape would leave the field out. Accept
     the MCP list shape the copy carried.
   - *The owner's:* the context, `equipment`. And the same path, tool names, and noun as projection,
     taken from there rather than decided again.
5. **Edits, each tagged.**
   - *Essential:* the test (6 lines). `REQUIREMENTS_v3` (19 diff lines against v2). The facts the
     change needs (path, collection, tag, nouns, two tool names, four fields) are in the new files,
     spread across 7 of the 10.
   - *Shape:* 9 new Kotlin files (165 lines) and 3 edits to `DeliveryConfig` (5 diff lines). Most
     of it repeats the neighbour's lines with a new name in them.
   - *Incidental:* `seed.py` (13 lines) and `golden.py` (1 line), the same as projection's.
     Citations and counts in a test comment, `DESIGN_v2`, `QUEUE`, and `CONTRIBUTING` (21 lines).
     The manifest is re-recorded (generated).
6. **Wrong turns.** None in the code. In `CONTRIBUTING.md`, two edits made by line number landed on
   the wrong lines after an earlier insert moved the text. Caught by reading the result, and redone
   by matching text.
7. **Done.** The new test, seen failing 3 of 3, passes. `pixi run check`: 81 of 81. Before the
   re-record, `pixi run golden` differed only in `tools/list`, as on projection. After it, the
   compare prints `GOLDEN MATCH`. **Both branches' manifests are identical for all 5,107 `rest` and
   `mcp` cases**, so the two shapes serve the same API, the new resource included. Only the
   OpenAPI document differs, and that was never held.
8. **The structure's part.** *For:* the neighbour answered every question of form, the compiler
   checked the new type across all three layers, and the OpenAPI document gained a response schema
   for the new resource without being asked. *Against:* ten files and eight forms of one name for
   a resource with four fields. And one registration that nothing but the golden master checks.
9. **Counts.** 18 files, counting the generated manifest. Lines a reviewer reads: 25 essential, 170
   for the shape, 14 incidental for data and tooling, and 21 incidental for citations. Kotlin
   changed in 10 files (170 lines), against projection's 1 file (6 lines) for behaviour.
   `pixi run check`: 81 of 81. `pixi run golden`: `GOLDEN MATCH` on 5,108 cases.
