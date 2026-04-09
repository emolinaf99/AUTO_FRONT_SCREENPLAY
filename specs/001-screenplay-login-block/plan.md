# Implementation Plan: Screenplay Login & Account Block Automation — HU2

**Branch**: `001-screenplay-login-block` | **Date**: 2026-04-08 | **Spec**: [spec.md](spec.md)  
**Input**: Feature specification from `/specs/001-screenplay-login-block/spec.md`

## Summary

Automate two HU2 scenarios for FrontendTicketing (`localhost:5173`) using the Serenity/BDD Screenplay pattern (Java 21, Serenity 4.2.0, Cucumber 7.18.0):

- **CP-HU2-01** (positive): login with valid credentials → JWT token stored in `localStorage['auth_token']` + redirect to `/`
- **CP-HU2-09** (negative): three consecutive failed login attempts with a fresh UUID-email account → account-blocked UI message visible

Technical approach: one `IniciarSesion` Task (SRP = perform a single login attempt via Actions), composed three times from the Step Definition for CP-HU2-09. Three Questions (`UrlActual`, `TokenGuardado`, `MensajeDeBloqueoCuenta`) assert observable state. Locators centralized in `LoginUi`. Fresh account for CP-HU2-09 created via a tagged `@Before` hook using Java 21 `HttpClient` calling the backend registration endpoint — no UI registration, no POM duplication.

## Technical Context

**Language/Version**: Java 21 (via Gradle `toolchain { languageVersion = JavaLanguageVersion.of(21) }`)  
**Primary Dependencies**: Serenity BDD 4.2.0 · serenity-screenplay 4.2.0 · serenity-screenplay-webdriver 4.2.0 · serenity-cucumber 4.2.0 · Cucumber Java 7.18.0 · JUnit 4.13.2 · Selenium WebDriver (Chrome) — **no new dependencies needed**  
**Storage**: `localStorage` key `auth_token` (FrontendTicketing `authStore.ts`) — read-only from test via JavaScript executor  
**Testing**: `./gradlew clean test aggregate` → Serenity single-page HTML report  
**Target Platform**: Chrome headed on Linux (serenity.conf `headless.mode = false`; override with `-Dheadless.mode=true` for CI)  
**Project Type**: BDD test automation suite (UI E2E, Screenplay pattern)  
**Performance Goals**: CP-HU2-01 < 10 s end-to-end · CP-HU2-09 < 30 s for three failed attempts + assertion  
**Constraints**: No new `build.gradle` dependencies · No POM pattern · No registration UI flow · SRP: one Task per business objective · Locators only in `*Ui.java` classes  
**Scale/Scope**: 2 automated scenarios (CP-HU2-01, CP-HU2-09) in one new feature file, extending existing suite without modifying `consulta_y_reserva_tickets.feature`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Rule (constitution.md) | Status | Notes |
|------|----------------------|--------|-------|
| No POM | POM pattern prohibited in this repo | ✅ PASS | All interactions via Screenplay Actions |
| SRP Tasks | Una Task = una responsabilidad | ✅ PASS | `IniciarSesion` = one business objective (login attempt); three-attempt sequence composed in Step Def, not in a super-Task |
| Tasks don't call Tasks | Composition goes in Step Definitions | ✅ PASS | Step Def calls `actor.attemptsTo(IniciarSesion.como(...))` three times for CP-HU2-09 |
| Locators centralized | All selectors in `*Ui.java` | ✅ PASS | `LoginUi.java` owns all login-page selectors |
| No registration duplication | HU1 scenarios owned by AUTO_FRONT_POM_FACTORY | ✅ PASS | CP-HU2-09 account created via `@Before` API hook (Java 21 `HttpClient`), not UI registration flow |
| Independent scenarios | Actor created in `@Before`, destroyed in `@After` | ✅ PASS | `HooksAutenticacion.java` manages Actor lifecycle |
| No new dependencies | `build.gradle` unchanged | ✅ PASS | Java 21 `HttpClient` is JDK-native; no additional libs needed |
| Semantic naming | Tasks/Questions/UI express business intent | ✅ PASS | `IniciarSesion`, `TokenGuardado`, `MensajeDeBloqueoCuenta`, `LoginUi` |
| No commented code | Zero `//TODO` or disabled code | ✅ REQUIRED | Enforced at implementation time |

**POST-DESIGN RE-CHECK**: All gates pass. No violations to justify.

## Project Structure

### Documentation (this feature)

```text
specs/001-screenplay-login-block/
├── plan.md              ← this file
├── research.md          ← Phase 0 output
├── data-model.md        ← Phase 1 output
├── quickstart.md        ← Phase 1 output
├── contracts/
│   └── sesion_usuario.gherkin.md   ← Phase 1 output
└── tasks.md             ← Phase 2 output (/speckit.tasks — NOT created here)
```

### Source Code (repository root)

```text
src/test/java/com/ticketing/
├── tasks/
│   └── IniciarSesion.java            ← NEW: business Task (login attempt SRP)
├── questions/
│   ├── UrlActual.java                ← NEW: current browser URL
│   ├── TokenGuardado.java            ← NEW: reads localStorage['auth_token']
│   └── MensajeDeBloqueoCuenta.java  ← NEW: blocked-account message visibility
├── ui/
│   └── LoginUi.java                  ← NEW: all login-page CSS selectors
├── stepdefinitions/
│   ├── ConsultaReservaTicketsStepDefinitions.java  ← EXISTING — do not modify
│   └── SesionUsuarioStepDefinitions.java           ← NEW: HU2 step defs
├── hooks/
│   └── HooksAutenticacion.java       ← NEW: @Before Actor setup + UUID account API call for CP-HU2-09
├── runners/
│   └── CucumberTestRunner.java       ← EXISTING — update glue + features paths if needed
└── util/
    └── CredencialesBuilder.java      ← NEW: UUID email factory + credential value objects

src/test/resources/
├── features/
│   ├── consulta_y_reserva_tickets.feature  ← EXISTING — do not modify
│   └── sesion_usuario.feature              ← NEW: CP-HU2-01, CP-HU2-09
├── serenity.conf                           ← EXISTING — do not modify
└── logback-test.xml                        ← EXISTING — do not modify
```

**Structure Decision**: Single-project layout extending the existing structure. All new files follow the constitution's package conventions. The `util/` directory (already present) accommodates `CredencialesBuilder`. No structural changes to existing files except `CucumberTestRunner` (hooks glue path confirmation).

## Complexity Tracking

> No constitution violations — this section documents one design decision that required justification.

| Decision | Why Chosen | Simpler Alternative Rejected Because |
|----------|------------|--------------------------------------|
| Java 21 `HttpClient` in `@Before` hook for CP-HU2-09 account creation | CP-HU2-09 needs a fresh account with a zero failed-attempt counter; UI registration would duplicate AUTO_FRONT_POM_FACTORY | Using a fixed pre-seeded account risks test pollution between runs if the account gets blocked and is not reset; UUID + API hook makes each run self-contained |
