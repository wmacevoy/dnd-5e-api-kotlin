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

## refactor/ddd

No entries yet.
