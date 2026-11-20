# Contributing to ExamForge

Team: `@mdsulaimanshakil` (lead/admin), `@alifalahad`, `@mehedii-13`.
The detailed project plan (architecture, API contract, schedule, task prompts) is shared by the lead outside the repository. This page is the short version everyone agrees to.

## Golden rules

1. Work only on your own branch, created from the latest `develop`: `feature/…`, `bugfix/…`, `refactor/…`, `test/…`, `docs/…`, `chore/…` (`hotfix/…` from `main`).
2. Never commit to `main` or `develop` directly (rulesets block it anyway).
3. Every change reaches `develop` through a pull request with **1 approval from a teammate** and **green CI** (`pr-title`, `backend`, `frontend`, `docker-build`).
4. PR titles follow [Conventional Commits](https://www.conventionalcommits.org): `feat(attempt): add idempotent submission`.
5. Keep branches ≤ 2 days old and PRs small (≈ ≤ 400 changed lines). Open a **draft PR** the day you start.
6. Sync with `develop` every morning (09:15) and evening (19:00): `git fetch && git merge origin/develop`.
7. Don't edit files owned by another developer (see `.github/CODEOWNERS`). Ask first.
8. Shared contracts (DB schema, entities, events, API shapes, `application.yml`, `pom.xml`, `package.json`) change only via a PR labelled `contract-change`, announced in chat first.
9. Never force-push shared branches. On your own branch only `--force-with-lease`, and not after review started.
10. Resolve conflicts locally, run the tests, then push.
11. Write tests **with** the feature, not after.
12. Never commit secrets. `.env` is ignored; use `.env.example` for placeholders.
13. The `docs/` folder is local-only and git-ignored — never force-add it.

## Merge methods

| Target | Method | Who merges |
|---|---|---|
| `develop` ← feature | **Squash** | Author (after approval + green CI; use auto-merge) |
| `develop` ← `main` (back-merge after hotfix) | Merge commit | Sulaiman |
| `main` ← `develop` (release) | **Merge commit** | Sulaiman (release manager), Alif as backup |

## Review expectations

- Reviews requested from you are done within **4 working hours** (windows 13:00–14:00 and 18:00–19:00).
- Critical areas (auth, security, timing, submission, grading, result publication) are reviewed by the other senior (Sulaiman ↔ Alif). If that person is unavailable for more than 4 hours, Mehedi may approve, the approver merges, and the senior does a post-merge review within 24 hours.
- Reviewer checklist: correctness, `@PreAuthorize` + ownership checks, no answer keys/secrets leaked, DTOs not entities, tests included, matches the API contract, only owned files changed, UI states (loading/empty/error, dark mode, mobile).

## Local setup

Prerequisites: Git, JDK 21 (Temurin), Node 24 LTS, Docker Desktop.

```bash
git clone https://github.com/examforge-team/examforge.git && cd examforge && git switch develop
docker compose -f docker-compose.dev.yml up -d           # PostgreSQL (+ Adminer on :8081)
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,demo
cd frontend && npm ci && npm run dev                      # http://localhost:5173
```

## Before pushing

```bash
cd backend && ./mvnw spotless:apply && ./mvnw verify
cd frontend && npm run format && npm run lint && npm run typecheck && npm run test:ci && npm run build
```
