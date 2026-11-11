# Queue

What is being worked, in what order, by whom, and what would make it done. The shared cursor
([SE1/W10](https://github.com/wmacevoy/software-engineering/blob/v1.7/guide/08-work.md#w10)).
Nothing enters without a *done* sentence that can be checked from outside — "it works" is not one.
An item that contradicts the requirements is a question for the client, not a task; mark it blocked,
and say on what. Only a human sets *done*.

Built on 2026-11-11 from the gaps found adopting the guide. Running the check confirmed items 3 and
4. Items 5 to 7 come from reading the code and have not been reproduced.

| # | Item | Versioned requirement IDs (or *chore*) | Owner | Done when | Estimate (number / range / time box) | Status | Since |
| - | ---- | -------------------------------------- | ----- | --------- | ------------------------------------ | ------ | ----- |
| 1 | Run the check for the first time | *chore* | unassigned | `pixi run check` has been run against a seeded MongoDB and its result is recorded in `docs/FINDINGS.md` | time box: 2 hours | review | 2026-11-11 |
| 2 | CI runs the check | *chore* | unassigned | A workflow runs the command `CONTRIBUTING.md` names, on every push, against seeded data, and `./se --doctor` shows CI as passing | time box: 1 day | ready | 2026-11-11 |
| 3 | The list test expects an array, but list endpoints return a paged object | REQ1/API-2 | unassigned | The shared list test asserts the items in `content`, and is seen to fail if the response shape changes | time box: 1 hour | review | 2026-11-11 |
| 4 | Proficiencies read the collection `2014-2014-proficiencies` | REQ1/API-1, REQ1/DAT-1 | unassigned | `GET /api/proficiencies/heavy-armor` returns that item, and a test naming REQ1/API-1 fails without the fix | time box: 1 hour | review | 2026-11-11 |
| 5 | GraphQL has a schema and no data fetchers | REQ1/GQL-1 | unassigned | Either the four queries return data under test, or the endpoint is gone and the README and requirements no longer promise it | range: 1 hour to 2 days | blocked: on the client's choice, build or drop | 2026-11-11 |
| 6 | Error body says status 500 on HTTP 400 responses | REQ1/API-4 | unassigned | The `status` in an error body equals the HTTP status, and a test naming the requirement fails without the fix | time box: 2 hours | blocked: on a requirement stating what errors should look like | 2026-11-11 |
| 7 | `GET /api/equipment` accepts `query` and ignores it | REQ1/API-6 | unassigned | The parameter either filters the list under test or is no longer accepted or documented | time box: 1 hour | blocked: on the client's choice | 2026-11-11 |
| 8 | Confirm the working set | REQ1/MISSION-1 | repository owner | Client, shape holder, mission, non-goals, and requirements are each confirmed or corrected, and their *(inferred)* marks removed | time box: 1 hour | ready | 2026-11-11 |
| 9 | Tests name the requirements | *chore* | unassigned | `./se --doctor` shows the trace passing | time box: half a day | blocked: on item 8 | 2026-11-11 |
| 10 | Tests carry their own database | *chore* | unassigned | `./mvnw -B verify` passes on a machine with no MongoDB running | range: half a day to 2 days | blocked: on the owner's approval of a new test dependency | 2026-11-11 |
