# ExamForge

> Online examination platform — server-enforced timing, idempotent submissions, automatic grading.
> Spring Boot · PostgreSQL · React · TypeScript · Tailwind · Docker

[![CI](https://github.com/examforge-team/examforge/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/examforge-team/examforge/actions/workflows/ci.yml)

## Features

- **Roles:** Admin, Teacher, Student with JWT authentication and refresh-token rotation
- **Question bank:** MCQ, multiple-select, numeric (tolerance), short answer; subjects, topics, tags, difficulty; search & filters; CSV import/export
- **Examinations:** scheduling windows, duration, passing marks, question/option shuffling, negative marking, attempt limits, access codes, publish/cancel/archive/duplicate
- **Exam taking:** distraction-free UI, server-synchronised countdown, autosave, bookmarks, navigation panel, resume after refresh, auto-submit on expiry
- **Grading & results:** automatic grading, teacher review of short answers, result publication, score breakdown, rank, history, CSV/PDF export
- **Analytics:** score distribution, averages, per-question difficulty & discrimination, top performers, participation
- **Notifications:** in-app notification centre, reminders, announcements, result alerts
- **Platform:** audit log, dark mode, responsive design, accessibility

## Repository layout

```
backend/    Spring Boot application (Java 21, Maven)
frontend/   React + Vite SPA
deploy/     Production Docker Compose, reverse proxy, scripts
.github/    CI/CD workflows, templates, CODEOWNERS
```

## Quick start (development)

```bash
docker compose -f docker-compose.dev.yml up -d
cd backend  && ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,demo
cd frontend && npm ci && npm run dev
```

- App: http://localhost:5173 · API docs: http://localhost:8080/swagger-ui.html
- Demo accounts (demo profile): see `backend/src/main/resources/db/seed/README.md`

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## Team

| Name | GitHub | Areas |
|---|---|---|
| Md. Sulaiman | [@mdsulaimanshakil](https://github.com/mdsulaimanshakil) | Architecture, security, exams, attempt engine, exam UI, DevOps |
| Alif | [@alifalahad](https://github.com/alifalahad) | Grading, results, analytics |
| Mehedi | [@mehedii-13](https://github.com/mehedii-13) | UI foundation, question bank & exam UIs, notifications, user admin, E2E |
