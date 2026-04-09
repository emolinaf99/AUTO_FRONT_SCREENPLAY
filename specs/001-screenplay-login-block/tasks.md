# Tasks: Screenplay Login & Account Block Automation — HU2

**Input**: Design documents from `specs/001-screenplay-login-block/`  
**Prerequisites**: [plan.md](plan.md) · [spec.md](spec.md) · [research.md](research.md) · [data-model.md](data-model.md) · [contracts/sesion_usuario.gherkin.md](contracts/sesion_usuario.gherkin.md) · [quickstart.md](quickstart.md)

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no shared dependency)
- **[US1]** / **[US2]**: Maps to User Story from spec.md
- No `[Story]` label = Setup or Foundational (shared infrastructure)

---

## Phase 1: Setup

**Purpose**: Wire the existing test runner and create the Gherkin contract file that drives all HU2 scenarios.

- [X] T001 Update `CucumberTestRunner.java` to include `com.ticketing.hooks` in the `glue` list in `src/test/java/com/ticketing/runners/CucumberTestRunner.java`
- [X] T002 Create `sesion_usuario.feature` with `@HU2`, `@CP-HU2-01`, and `@CP-HU2-09` tagged scenarios in Spanish as defined in the Gherkin contract in `src/test/resources/features/sesion_usuario.feature`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared value objects, locators, and Actor lifecycle infrastructure that MUST exist before either User Story can be implemented.

**⚠️ CRITICAL**: No User Story work can begin until this phase is complete.

- [X] T003 Create `Credenciales.java` immutable value object with `email`, `password` fields and `toJson()` serialization method in `src/test/java/com/ticketing/util/Credenciales.java`
- [X] T004 Create `CredencialesBuilder.java` with `conEmailUUID()` factory, `ThreadLocal<Credenciales>` store, `guardar()`, `obtener()`, and `limpiar()` methods in `src/test/java/com/ticketing/util/CredencialesBuilder.java`
- [X] T005 [P] Create `LoginUi.java` with `Target` constants `CAMPO_EMAIL`, `CAMPO_PASSWORD`, `BOTON_INGRESAR`, and `MENSAJE_BLOQUEO` — verify each selector against the running FrontendTicketing DOM at `localhost:5173/login` before finalizing in `src/test/java/com/ticketing/ui/LoginUi.java`
- [X] T006 Create `HooksAutenticacion.java` with untagged `@Before` (initialize Actor with `BrowseTheWeb` ability) and `@After` (quit driver + call `CredencialesBuilder.limpiar()`) hooks in `src/test/java/com/ticketing/hooks/HooksAutenticacion.java`

**Checkpoint**: Foundation ready — User Story phases can proceed independently.

---

## Phase 3: User Story 1 — CP-HU2-01 Successful Login (Priority: P1) 🎯 MVP

**Goal**: Actor navigates to login page, submits valid credentials → JWT token stored in `localStorage['auth_token']` and browser URL resolves to `/`.

**Independent Test**:
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-01"
```
Pass criteria: scenario green in Serenity report; `localStorage['auth_token']` non-null; URL contains `/`.

- [X] T007 [P] [US1] Create `UrlActual.java` Question that returns `BrowseTheWeb.as(actor).getDriver().getCurrentUrl()` in `src/test/java/com/ticketing/questions/UrlActual.java`
- [X] T008 [P] [US1] Create `TokenGuardado.java` Question that reads `window.localStorage.getItem('auth_token')` via `executeScript` through `BrowseTheWeb.as(actor).getDriver()` — constant `STORAGE_KEY = "auth_token"` in `src/test/java/com/ticketing/questions/TokenGuardado.java`
- [X] T009 [US1] Create `IniciarSesion.java` Task implementing `Performable` with static factory `como(String email, String password)` — internal actions: `Navigate.to("http://localhost:5173/login")`, `Enter.theValue(email).into(LoginUi.CAMPO_EMAIL)`, `Enter.theValue(password).into(LoginUi.CAMPO_PASSWORD)`, `Click.on(LoginUi.BOTON_INGRESAR)` in `src/test/java/com/ticketing/tasks/IniciarSesion.java`
- [X] T010 [US1] Create `SesionUsuarioStepDefinitions.java` with CP-HU2-01 step bindings: inject Actor via constructor, call `IniciarSesion.como(validEmail, validPassword)`, assert `UrlActual.delNavegador()` and `TokenGuardado.enLocalStorage()` in `src/test/java/com/ticketing/stepdefinitions/SesionUsuarioStepDefinitions.java`
- [X] T011 [US1] Confirm that CP-HU2-01 valid credentials are supplied exclusively via system properties (`-Dtest.credentials.email=` and `-Dtest.credentials.password=`) at runtime — `src/test/resources/serenity.conf` MUST NOT be modified to add credential values; update `src/test/java/com/ticketing/stepdefinitions/SesionUsuarioStepDefinitions.java` to read credentials via `System.getProperty("test.credentials.email")` and `System.getProperty("test.credentials.password")`

**Checkpoint**: `@CP-HU2-01` passes end-to-end in under 10 s. User Story 1 is independently verifiable.

---

## Phase 4: User Story 2 — CP-HU2-09 Account Block (Priority: P2)

**Goal**: Three consecutive failed login attempts with a fresh UUID-email account → account-blocked UI message is visible on screen.

**Independent Test**:
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-09"
```
Pass criteria: scenario green in Serenity report; `MensajeDeBloqueoCuenta.esVisible()` returns `true` after the third failed attempt.

- [X] T012 [P] [US2] Create `MensajeDeBloqueoCuenta.java` Question implementing `Question<Boolean>` that returns `LoginUi.MENSAJE_BLOQUEO.resolveFor(actor).isVisible()` with static factory `esVisible()` in `src/test/java/com/ticketing/questions/MensajeDeBloqueoCuenta.java`
- [X] T013 [US2] Add `@Before("@CP-HU2-09")` hook to `HooksAutenticacion.java` — generate UUID credentials via `CredencialesBuilder.conEmailUUID()`, POST to `http://localhost:8003/api/auth/register` (AuthService port, NOT the Vue 3 frontend port 5173) using Java 21 `HttpClient`, store via `CredencialesBuilder.guardar(creds)` in `src/test/java/com/ticketing/hooks/HooksAutenticacion.java`
- [X] T014 [US2] Add CP-HU2-09 step implementations to `SesionUsuarioStepDefinitions.java` — three sequential `actor.attemptsTo(IniciarSesion.como(email, wrongPassword))` calls (one per `Cuando`/`Y` step) and final `actor.should(seeThat(MensajeDeBloqueoCuenta.esVisible(), is(true)))` assertion in `src/test/java/com/ticketing/stepdefinitions/SesionUsuarioStepDefinitions.java`

**Checkpoint**: `@CP-HU2-09` passes end-to-end in under 30 s. User Story 2 is independently verifiable.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Full-suite validation and constitution compliance verification.

- [X] T015 Run `./gradlew clean test aggregate` and confirm both `@CP-HU2-01` and `@CP-HU2-09` pass with 0 failures; confirm Serenity single-page HTML report is generated at `target/site/serenity/index.html`
- [X] T016 [P] Review all new classes (`IniciarSesion`, `UrlActual`, `TokenGuardado`, `MensajeDeBloqueoCuenta`, `LoginUi`, `SesionUsuarioStepDefinitions`, `HooksAutenticacion`, `CredencialesBuilder`, `Credenciales`) for zero commented code, zero `//TODO`, one-responsibility-per-Task, and no selectors outside `LoginUi.java`
- [X] T017 [P] Verify explicit wait strategy across all new classes — confirm no `Thread.sleep` is present; confirm that `WebDriverWait` or Serenity's built-in element wait is applied to all element interactions declared in `LoginUi.java` targets; check that `IniciarSesion` and all Questions wait for element visibility before interacting (SC-001 flakiness prevention)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies — start immediately
- **Phase 2 (Foundational)**: Depends on Phase 1 · Blocks Phases 3 and 4
- **Phase 3 (US1)**: Depends on Phase 2 — T007 and T008 can start in parallel once Phase 2 is done
- **Phase 4 (US2)**: Depends on Phase 2 — T012 can start in parallel once Phase 2 is done; T013 requires T004; T014 requires T010, T012, T013
- **Phase 5 (Polish)**: Depends on Phases 3 and 4

### User Story Dependencies

| Story | Depends on | Independent from |
|-------|-----------|-----------------|
| US1 (CP-HU2-01) | Phase 2 complete | US2 — no shared state |
| US2 (CP-HU2-09) | Phase 2 complete + T010 (Step Def file exists to extend) | US1 result state — UUID account starts fresh |

### Within Each Story

- Phases 3/4: Questions and Task (`[P]` marked) can be written simultaneously — different files
- Step Definitions require Questions + Task + Hooks to be compiled first
- Selector confirmation (T005) must occur before `IniciarSesion` (T009) or any Question is run for the first time

### Critical Path

```
T001 → T002 → T003 → T004 → T005 → T006
                                         ↓
              T007 ──────────────────► T010 → T011 → [CP-HU2-01 ✓]
              T008 ──────────────────►
              T009 ──────────────────►
                                         ↓
              T012 ──────────────────► T014 → [CP-HU2-09 ✓]
              T013 ──────────────────►
                                         ↓
                                       T015 → T016
```

---

## Parallel Opportunities

### Phase 2 — run together (different files):
- T003 `Credenciales.java`
- T005 `LoginUi.java`

### Phase 3 — run together after T005/T006 complete (different files):
- T007 `UrlActual.java`
- T008 `TokenGuardado.java`
- T009 `IniciarSesion.java`

### Phase 4 — run together with Phase 3 (different files):
- T012 `MensajeDeBloqueoCuenta.java` (can start immediately after Phase 2)

### Phase 5 — run together:
- T015 (build) then T016 (code review) — T016 is independent of T015

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001, T002)
2. Complete Phase 2: Foundational (T003 → T006)
3. Complete Phase 3: User Story 1 (T007 → T011)
4. **STOP and VALIDATE**: `./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-01"`
5. CP-HU2-01 green = MVP delivered

### Incremental Delivery

1. Setup + Foundational → infrastructure ready
2. User Story 1 → test independently → CP-HU2-01 ✓ (MVP)
3. User Story 2 → test independently → CP-HU2-09 ✓
4. Polish → full suite green

---

## Notes

- **No new `build.gradle` dependencies**: Java 21 `HttpClient` (T013) is JDK-native
- **`consulta_y_reserva_tickets.feature`** must not be touched at any point — EXISTING file
- **`CucumberTestRunner.java`** (T001): only add `"com.ticketing.hooks"` to `glue` if not already present — do not remove existing glue entries
- **Selector confirmation** (T005): open `localhost:5173/login` in Chrome DevTools and inspect the email input, password input, submit button, and blocked-account message element before writing `LoginUi.java` constants
- **`ThreadLocal` cleanup** (T006/T013): `CredencialesBuilder.limpiar()` MUST be called in `@After` to prevent memory leaks in sequential test runs
- Tasks marked `[P]` can be assigned to different developers or tackled by the same developer in any order — they produce different files with no compile-time dependency on each other at creation time (dependencies arise at method-call level, enforced at compile of the Step Definition)
