# Contributing

How to work on this repository, for a new teammate and for an agent, who both arrive knowing
nothing
([SE1/A3](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/10-agents.md#a3)). Keep
it short and true: a change that makes a line here untrue updates it in the same change
([SE1/W11](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w11)).

**Status, 2026-11-11:** the check runs and currently fails, 26 of 75 tests (see `docs/FINDINGS.md`).

## Run it

- **Prerequisites:** [pixi](https://pixi.sh). `pixi install` provides JDK 21, MongoDB 7, and
  `mongoimport` under `.pixi/`, from `pixi.lock`. Maven comes from the wrapper, which caches in
  `~/.m2`.
- **Database:** `pixi run db-start`, then `pixi run seed` once. The seed loads the 2014 SRD from
  [5e-bits/5e-database](https://github.com/5e-bits/5e-database) at a pinned commit, into
  `localhost:27017`. `pixi run db-stop` stops it.
- **Build:** `pixi run sh ./mvnw -B -DskipTests package`
- **Run:** `pixi run sh ./mvnw spring-boot:run` — the server starts on Spring Boot's default port,
  8080, and Swagger UI is at `http://localhost:8080/swagger-ui/index.html`. Not yet run.
- **Reset to a known state:** `pixi run sh ./mvnw clean` wipes `target/`, in seconds. `pixi run seed`
  drops and reloads every collection. The application never writes to MongoDB, so running it or its
  tests does not dirty the data.

`mvnw` is committed without its executable bit, so call it as `sh ./mvnw`; the `./` matters, because
the wrapper finds its own directory from that path.

## Check it

- **The full check:** `pixi run check` — the same one CI will run, once CI exists (queue item 2)
  ([SE1/O3](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/07-operating.md#o3)).
  It needs the running, seeded database above, because the tests are `@SpringBootTest`s with no test
  database of their own.
- **Success prints:** `BUILD SUCCESS`
- **Instruments:** none yet. `pom.xml` configures no coverage tool or linter.

## Definition of done

The list under *Working* in the guide's
[CHECKLISTS.md](https://github.com/wmacevoy/software-engineering/blob/v1.7/CHECKLISTS.md#working),
all of it, with no line dropped
([SE1/W11](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w11)).

## Rules for this repository

- `vendor/se1/` and the root `AGENTS.md`, `se`, `se.ps1`, and `se.py` are copies of guide release
  v1.7. Never edit them. Local rules go here, and this file wins where they disagree. To move to a
  new release, re-copy as the guide's `ADOPT.md` step 1 says.
- The database is another project's schema. A collection or field renamed in 5e-database breaks
  this API, so a change to a `@Document(collection = ...)` name is a one-way door (`DESIGN_v1.md`).
- A change in a requirement's meaning is a new version of the requirements file.

## Where things are

- **Working set:** `docs/` holds `REQUIREMENTS_v1`, `DESIGN_v1`, `QUEUE`, `FINDINGS`
  ([SE1/W10](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w10)).
  Most of it was rebuilt from the code and is marked *(inferred)* until a person confirms it.
- **Agents:** read `AGENTS.md` in this repository (copied from the
  [guide](https://github.com/wmacevoy/software-engineering/blob/v1.7/AGENTS.md)) and this file
  before acting. `CLAUDE.md` imports `AGENTS.md` and `vendor/se1/QUICKREF.md` at every start.
- **Code:** one package per SRD resource under `src/main/kotlin/com/thejaxonhill/dnd5eapi/`, each
  split into `domain`, `application`, and `infrastructure`; what they share is in `shared/`.

## What AGENTS.md's nouns mean here

| AGENTS.md says                 | In this repository                                    |
| ------------------------------ | ----------------------------------------------------- |
| the repository guide           | `CONTRIBUTING.md`                                     |
| the queue                      | `docs/QUEUE.md`                                       |
| findings                       | `docs/FINDINGS.md`                                    |
| the check command              | `pixi run check`; success prints `BUILD SUCCESS`      |
| the handoff check              | the `SessionStart` hook running `se.py --post`        |
| `Non-goals`, `Scope decisions` | sections of `docs/REQUIREMENTS_v1.md`                 |
