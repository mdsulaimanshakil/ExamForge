<!--
PR title must be a Conventional Commit, e.g. "feat(attempt): add idempotent submission".
Keep PRs small (≈ ≤ 400 changed lines excluding lockfiles). Open as Draft early.
-->

## Summary

<!-- What does this PR do and why? 2–4 sentences. -->

**Work package:** <!-- e.g. S06 — Exam attempt engine (part A) -->
**Closes:** #<!-- issue number -->

## Type of change

- [ ] feat  - [ ] fix  - [ ] test  - [ ] refactor  - [ ] docs  - [ ] chore/ci/build  - [ ] release

## Changes

-
-

## How to test

<!-- Exact steps or commands, seed users, endpoints/pages to open. -->

```bash
# e.g.
cd backend && ./mvnw verify
```

## Screenshots (UI changes: light + dark, desktop + 375 px)

## Checklist (author)

- [ ] Branch is up to date with `develop`; CI is green
- [ ] Only files I own changed — or the owner is tagged and agreed (`@…`)
- [ ] API matches `docs/plan/07-api-contract.md` (or this PR updates it and is labelled `contract-change`)
- [ ] New/changed endpoints have `@PreAuthorize` + ownership checks
- [ ] Tests added: unit ☐ integration ☐ component ☐ (list the most important below)
- [ ] No secrets, tokens, answer keys or passwords in code, logs or responses
- [ ] New migration uses timestamp naming; no merged migration edited
- [ ] UI: loading / empty / error states, dark mode, mobile, keyboard focus
- [ ] Docs/Swagger annotations updated where relevant

**Key tests:**

-

## Critical area? (auth, security, timing, submission, grading, result publication)

- [ ] Yes → label `critical-area`; reviewer is the other senior (Sulaiman ↔ Alif) or fallback per §10.3
- Concurrency / edge cases covered:
