# Feature Specification: Screenplay Login & Account Block Automation — HU2

**Feature Branch**: `001-screenplay-login-block`  
**Created**: 2026-04-08  
**Status**: Draft  
**Input**: User description: "Automatizar con patrón Screenplay (Actor/Task/Question) el flujo de login y bloqueo de cuenta de FrontendTicketing en localhost:5173. HU2 — Escenario positivo CP-HU2-01: login exitoso con credenciales válidas guarda token JWT y redirige a /. Escenario negativo CP-HU2-09: tercer intento fallido consecutivo bloquea la cuenta y muestra mensaje de bloqueo (HTTP 423). Los escenarios de registro son propiedad de AUTO_FRONT_POM_FACTORY y no deben replicarse aquí. SRP obligatorio: una Task por responsabilidad."

## Clarifications

### Session 2026-04-08

- Q: ¿Cómo se garantiza que la cuenta usada en CP-HU2-09 tenga el contador de intentos en 0 al inicio? → A: Cuenta dedicada por ejecución — email generado con UUID (mismo patrón que CP-HU1-08 en POM). La cuenta nunca ha fallado antes, por lo que el contador arranca en 0. Los 3 intentos fallidos se acumulan dentro del mismo escenario como pasos `When` consecutivos. Sin dependencia de endpoint de reset ni de estado previo en BD.
- Q: ¿Los campos vacíos (usuario/contraseña en blanco) están en alcance de esta suite? → A: Fuera de alcance — pertenecen a AUTO_FRONT_POM_FACTORY. Son validación de formulario cliente (equivalente CP-HU1-02 para login); si se automatizan en UI van en el repo POM, no aquí.
- Q: ¿Cuál es el navegador objetivo y el modo de ejecución (headless/headed)? → A: Configurable externamente — el proyecto ya tiene `headless.mode = false` en `serenity.conf` (Chrome headed por defecto). El spec no fija navegador ni modo; ambos se gestionan exclusivamente en `serenity.conf`, anulable vía `-Dheadless.mode=true` para CI.
- Q: ¿Dónde y bajo qué clave almacena el frontend el JWT tras el login? → A: `localStorage`, clave exacta `auth_token` — definido en `authStore.ts` del FrontendTicketing (`localStorage.setItem('auth_token', token)`). La `Question` `JwtTokenInStorage` debe leer `window.localStorage.getItem('auth_token')`.
- Q: ¿Cómo verifica la suite el HTTP 423 del tercer intento fallido — interceptación de red, llamada REST paralela, o solo aserción de UI? → A: Solo aserción de UI. La `Question` `BlockedAccountMessageVisibility` observa únicamente lo que el usuario experimenta en pantalla. La verificación HTTP 423 en capa de red pertenece a AUTO_API_SCREENPLAY (CP-HU2-09 vía API). Mezclar interceptación de red en un test UI violaría la separación de responsabilidades entre los 3 repositorios.

## User Scenarios & Testing *(mandatory)*

### User Story 1 — Successful Login Flow, CP-HU2-01 (Priority: P1)

A registered user opens the FrontendTicketing login page, enters valid credentials, and is authenticated. After authentication, the application stores the issued JWT token and navigates the user to the application home route (`/`). The test suite verifies this complete flow end-to-end using the Screenplay pattern, with discrete Tasks for navigation, credential entry, and form submission, and a Question to assert the resulting state.

**Why this priority**: The happy-path login is the gateway to all other authenticated features. Without a verified positive login flow, no downstream automation can run reliably. It is the single most critical scenario for the suite.

**Independent Test**: Can be fully tested by executing only `CP-HU2-01` against a running FrontendTicketing instance with a pre-seeded valid user, confirming that the JWT token is present in storage and the browser URL has resolved to `/`.

**Acceptance Scenarios**:

1. **Given** the FrontendTicketing application is running at `localhost:5173` and a user account with valid credentials exists, **When** the actor navigates to the login page and submits the correct username and password, **Then** the application stores a non-empty JWT token and the current URL is `/`.
2. **Given** the actor is on the login page, **When** valid credentials are submitted, **Then** no error message is displayed on screen.
3. **Given** the actor has just logged in successfully, **When** the JWT token presence is checked, **Then** `localStorage.getItem('auth_token')` returns a non-null, non-empty string.

---

### User Story 2 — Account Block After Third Consecutive Failed Login, CP-HU2-09 (Priority: P2)

A user attempts to log in three times consecutively using incorrect credentials. On the third failed attempt, the system blocks the account and returns an HTTP 423 status. The application displays a visible account-blocked message to the user. The test suite verifies the full three-attempt sequence and the resulting blocked state using the Screenplay pattern, with discrete Tasks for each login attempt and a Question to assert the blocking message visibility.

**Why this priority**: Account blocking is a critical security control. It is testable independently of the positive flow, but depends on the login page being functional, hence P2.

**Independent Test**: Can be fully tested by executing only `CP-HU2-09` against a running FrontendTicketing instance. The scenario self-provisions a fresh account via UUID-generated email (never previously used), so the failed-attempt counter always starts at 0. The three consecutive failures are performed within the scenario itself, and the final assertion confirms the blocking message is displayed.

**Acceptance Scenarios**:

1. **Given** the FrontendTicketing application is running and a freshly registered account with a UUID-generated email exists (failed-attempt counter = 0), **When** the actor submits wrong credentials once, **Then** an inline error message is displayed and the account remains unlocked.
2. **Given** the actor has already failed login twice with the same account, **When** the actor submits wrong credentials a third time, **Then** the application displays an account-blocked message.
3. **Given** the account is blocked, **When** the actor tries to log in again with the correct credentials, **Then** the blocked-account message is still shown (the account cannot be unblocked from the login page alone).

---

### Edge Cases

- ~~What happens when the user submits an empty username or password field?~~ **Out of scope** — client-side form validation; owned by AUTO_FRONT_POM_FACTORY.
- ~~What happens when the network is unavailable during login submission?~~ **Out of scope** — HU2 v1, not automated in this iteration.
- ~~What happens if the JWT token (`auth_token`) already exists in `localStorage` before the login attempt?~~ **Out of scope** — HU2 v1, not automated in this iteration.
- How does the system behave if the FrontendTicketing app is not reachable at `localhost:5173` when the test starts? *(infrastructure pre-condition; not an automated scenario)*
- ~~What happens if only two consecutive failed attempts are made (boundary before block)?~~ **Out of scope** — HU2 v1, not automated in this iteration.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The automation suite MUST implement the Screenplay pattern with distinct Actor, Task, and Question components for every automated action and assertion.
- **FR-002**: Each Task MUST encapsulate exactly ONE business objective. Multiple UI actions may live inside one Task when they are inseparable steps toward that single objective. Tasks MUST NOT manage two distinct business objectives.
- **FR-003**: The suite MUST automate scenario CP-HU2-01: the actor navigates to the login page, enters valid credentials, submits the form, and the post-login assertions confirm that a JWT token is stored and the route resolves to `/`.
- **FR-004**: The suite MUST automate scenario CP-HU2-09: the actor performs three consecutive login attempts with invalid credentials against the same account, and the post-third-attempt assertions confirm the account-blocked message is displayed.
- **FR-005**: The suite MUST confirm account blocking exclusively through the visible blocked-account UI message (`MensajeDeBloqueoCuenta` Question); HTTP 423 network-layer verification is out of scope for this suite and is owned by AUTO_API_SCREENPLAY.
- **FR-006**: The suite MUST NOT replicate or duplicate any registration scenarios; those are owned by the AUTO_FRONT_POM_FACTORY suite.
- **FR-007**: The suite MUST target the FrontendTicketing application at `localhost:5173`.
- **FR-008**: Questions MUST be technology-agnostic assertions that can query navigated URL state, token presence, and visible page text without being coupled to any particular UI component library.
- **FR-009**: Test data (valid credentials, invalid credentials) MUST be externalisable so they can be changed without modifying Task or Question classes. For CP-HU2-09, the username MUST be a UUID-generated email registered fresh per execution; this guarantees a zero failed-attempt counter without any reset dependency.

### Key Entities

- **Actor**: Represents a user persona interacting with the system; holds the ability to browse the web and remembers facts (e.g., stored JWT token value).
- **Task — IniciarSesion**: Single business objective: navigate to login page, fill credentials, and submit the form. Navigation + credential entry + submission are inseparable steps of one login attempt and therefore belong in one Task (FR-002). **Note**: the composition of three consecutive failed attempts for CP-HU2-09 happens in the Step Definition, not inside this Task.
- **Question — UrlActual**: Returns the browser's current URL so the actor can assert redirection.
- **Question — TokenGuardado**: Reads `window.localStorage.getItem('auth_token')` and returns the value. Key `auth_token` is canonical per `authStore.ts` of FrontendTicketing.
- **Question — MensajeDeBloqueoCuenta**: Returns whether the account-blocked message is visible on screen.
- **Credentials (test data)**: Value object carrying `username` (UUID-generated email for block scenario; fixed valid email for login scenario) and `password`; sourced from external configuration. The UUID email pattern guarantees a fresh account with a zero failed-attempt counter for CP-HU2-09 without requiring any reset mechanism.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Both automated scenarios (CP-HU2-01 and CP-HU2-09) pass consistently across 10 consecutive suite runs with 0 flaky failures.
- **SC-002**: The positive login scenario (CP-HU2-01) completes end-to-end in under 10 seconds on a standard developer machine.
- **SC-003**: The account-block scenario (CP-HU2-09) completes all three login attempts and the final assertion in under 30 seconds on a standard developer machine.
- **SC-004**: No Task class contains more than one distinct user responsibility, verifiable by code review — each Task maps to exactly one action (navigation, input, submission).
- **SC-005**: Zero registration-related scenarios exist anywhere in this suite's test files.
- **SC-006**: The account-blocked UI message is asserted by the `MensajeDeBloqueoCuenta` Question and reported in the Serenity test output; HTTP 423 network-layer verification is delegated to AUTO_API_SCREENPLAY and is not asserted here.

## Assumptions

- The FrontendTicketing application is already running and accessible at `localhost:5173` when the suite executes; the suite does not start or stop the application.
- A pre-seeded valid user account exists in the backend before CP-HU2-01 runs; test data setup is outside the scope of this suite.
- For CP-HU2-09, a fresh account with a UUID-generated email is registered at the start of each scenario execution (same pattern as CP-HU1-08 in AUTO_FRONT_POM_FACTORY). The account has never failed a login attempt, so the failed-attempt counter is always 0 without any reset mechanism or external state dependency. The three consecutive failures accumulate within the scenario itself as sequential `When` steps.
- The account-blocking policy is exactly three consecutive failed attempts; this threshold is not configurable by the suite.
- JWT token is stored in browser `localStorage` under the key `auth_token`, as defined in `authStore.ts` of FrontendTicketing (`localStorage.setItem('auth_token', token)`). The `TokenGuardado` Question reads exactly this key.
- Browser choice and headless/headed mode are not defined in this spec; they are governed solely by `serenity.conf` (`headless.mode = false` by default — Chrome headed) and can be overridden at runtime via the system property `-Dheadless.mode=true` for CI environments. The spec must not duplicate or override this configuration.
- The Serenity/BDD + Screenplay library stack already present in the project (`build.gradle`) is the target framework for implementation.
- Registration flows are fully covered by AUTO_FRONT_POM_FACTORY; this suite treats them as out of scope and does not import or extend those test classes.
- This suite is one of three repositories with distinct automation responsibilities: AUTO_FRONT_POM_FACTORY (registration/POM UI), AUTO_FRONT_SCREENPLAY (this suite — UI Screenplay flows), and AUTO_API_SCREENPLAY (API-level assertions including HTTP 423 on CP-HU2-09). No cross-repository concern mixing is permitted.
- The AuthService (backend) is available at `http://localhost:8003` during suite execution. The `@Before("@CP-HU2-09")` hook sends the UUID account registration request to `http://localhost:8003/api/auth/register`. This is a separate process from the FrontendTicketing Vue 3 app running at `localhost:5173`.
