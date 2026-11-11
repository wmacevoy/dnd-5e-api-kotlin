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

No entries yet.

## refactor/ddd

### Change 1 — a 26th resource, the 2024 weapon mastery properties

1. **The change and who made it.** As written in `EXPERIMENT.md`. Made on 2026-11-11 by Claude Opus
   5.5, which had built all three shapes that day and made this change twice already. Every question
   outside the code was answered, and the shared edits were applied from projection's. So this
   entry measures only what the shape adds, and it is a floor.
2. **Questions before code.**
   - *Is a weapon mastery property a new kind of entry?* Yes: one row in `KINDS`. The shape asks for
     the kind's nouns, not its tool names or tag, and those follow from the nouns.
   - *Where does "2024" belong?* This was the one real question, and it has two owners. That a kind
     comes from the 2024 SRD is a fact in the Compendium's language, so it goes on `Kind` as
     `edition`. That the collection is spelled `2024-...` is upstream's spelling, so it stays in the
     storage adapter, which now reads the edition. Found by asking the shape, not by searching.
   - *Does any surface need an exception?* No. The tool names, tag, path, and summaries all follow
     the pattern, so `rest/`, `mcp/`, and `published/` need nothing.
3. **Held at once.** Three things. The kind's nouns and article. That the edition is both a model fact
   and part of a collection name. And the shared edits, already done. If forgotten: a wrong noun gives
   a wrong tool name, which only the golden master's `tools/list` would catch. A missing edition
   reads an empty `2014-` collection, which the new test catches.
4. **Decisions.**
   - *Mine:* `edition` is a field with a default of 2014, rather than a separate list of 2024 kinds.
   - *The owner's, raised in the code:* whether edition is part of an entry's identity. Upstream's
     2024 SRD reuses 2014 names, such as `weapon-properties`. Serving one of those would put two
     kinds under one path. The same collision with the 2014-only scope decision as on the other
     branches, still open.
5. **Edits, each tagged.**
   - *Essential:* the `KINDS` row (1 line). The test (6 lines). `REQUIREMENTS_v3` (20 diff lines
     against v2).
   - *Shape:* the `edition` field on `Kind` with its comment (6 lines), and the adapter's collection
     name reading it (1 line). On projection the collection was a literal in the row, so it needed
     neither. Here the year is a fact the model owns, and upstream's spelling of it stays in the
     adapter.
   - *Incidental:* `seed.py` (13 lines) and `golden.py` (1 line), as on the other branches. Citations
     in three code comments, a test comment, `QUEUE`, and `CONTRIBUTING` (19 lines).
6. **Wrong turns.** One. The first comment on `edition` said no two editions share a kind's name.
   That is false, because upstream's 2024 SRD has its own `weapon-properties`. Caught by checking the
   claim against upstream's file list before committing; no test or check would have caught it.
7. **Done.** The new test, seen failing 3 of 3, passes. `pixi run check`: 81 of 81. Before the
   re-record, `pixi run golden` differed only in `tools/list`. After it, `GOLDEN MATCH`. **The
   manifest equals both other branches' for all 5,107 `rest` and `mcp` cases.**
8. **The structure's part.** *For:* one row, because each surface derives its names from the nouns
   and writes only its exceptions; and the shape asked the one question worth asking, where the year
   belongs. *Against:* a kind whose noun does not give its tool name would need an exception in
   `mcp/`, and only the golden master would notice it missing.
9. **Counts.** 12 files, counting the generated manifest. Lines a reviewer reads: 27 essential, 7 for
   the shape, 14 incidental for data and tooling, and 19 incidental for citations. Kotlin changed in
   2 files for behaviour (8 lines) and in 3 for comments. `pixi run check`: 81 of 81.
   `pixi run golden`: `GOLDEN MATCH` on 5,108 cases.
