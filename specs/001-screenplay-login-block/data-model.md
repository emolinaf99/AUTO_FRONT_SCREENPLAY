# Data Model: Screenplay Login & Account Block Automation — HU2

**Phase**: 1 — Design  
**Branch**: `001-screenplay-login-block`  
**Date**: 2026-04-08

---

## Entities

### 1. `Actor`

Represents the user persona interacting with FrontendTicketing.

| Attribute | Type | Description |
|-----------|------|-------------|
| name | `String` | Persona label (e.g., `"Esteban"`) — used in Serenity reports |
| abilities | `List<Ability>` | `[BrowseTheWeb.with(driver)]` — one ability per scenario |

**Lifecycle**: Created fresh in `@Before` (HooksAutenticacion); destroyed in `@After`. Never shared between scenarios.

**Relationships**: Uses → Tasks (via `attemptsTo`); queries → Questions (via `should(seeThat(...))`).

---

### 2. `IniciarSesion` (Task)

Business Task: perform one complete login attempt (navigate + fill form + submit).

| Attribute | Type | Description |
|-----------|------|-------------|
| email | `String` | Username/email to fill in the login form |
| password | `String` | Password to fill in the login form |

**Factory**: `IniciarSesion.como(String email, String password) → IniciarSesion`

**Internal Actions** (in order):
1. `Navigate.to("http://localhost:5173/login")`
2. `Enter.theValue(email).into(LoginUi.CAMPO_EMAIL)`
3. `Enter.theValue(password).into(LoginUi.CAMPO_PASSWORD)`
4. `Click.on(LoginUi.BOTON_INGRESAR)`

**SRP boundary**: one business objective = one login attempt. Does not check outcomes. Does not call other Tasks.

---

### 3. `UrlActual` (Question)

Queries the browser's current URL after navigation.

| Attribute | Type | Description |
|-----------|------|-------------|
| — | — | Stateless |

**Returns**: `String` — current URL (e.g., `"http://localhost:5173/"`)  
**Factory**: `UrlActual.delNavegador() → UrlActual`  
**Assertion pattern**: `actor.should(seeThat(UrlActual.delNavegador(), containsString("/")))`

---

### 4. `TokenGuardado` (Question)

Reads `window.localStorage.getItem('auth_token')` via JavaScript executor.

| Attribute | Type | Description |
|-----------|------|-------------|
| STORAGE_KEY | `static final String` | `"auth_token"` — canonical per `authStore.ts` |

**Returns**: `String` — token value, or `null` if not set  
**Factory**: `TokenGuardado.enLocalStorage() → TokenGuardado`  
**Assertion pattern**:
```java
actor.should(seeThat(TokenGuardado.enLocalStorage(), is(notNullValue())));
actor.should(seeThat(TokenGuardado.enLocalStorage(), not(emptyOrNullString())));
```

---

### 5. `MensajeDeBloqueoCuenta` (Question)

Checks whether the account-blocked message is displayed on the login page.

| Attribute | Type | Description |
|-----------|------|-------------|
| — | — | Stateless |

**Returns**: `Boolean` — `true` if the blocked-account message element is visible in the DOM  
**Factory**: `MensajeDeBloqueoCuenta.esVisible() → Question<Boolean>`  
**Assertion pattern**: `actor.should(seeThat(MensajeDeBloqueoCuenta.esVisible(), is(true)))`

---

### 6. `LoginUi` (UI Locators)

Centralized CSS/XPath selectors for the FrontendTicketing login page.

| Constant | Type | Selector strategy | Semantic meaning |
|----------|------|-------------------|-----------------|
| `CAMPO_EMAIL` | `Target` | `input[name="email"]` or `[data-testid="login-email"]` | Email input field |
| `CAMPO_PASSWORD` | `Target` | `input[name="password"]` or `[data-testid="login-password"]` | Password input field |
| `BOTON_INGRESAR` | `Target` | `button[type="submit"]` or `[data-testid="login-submit"]` | Submit/login button |
| `MENSAJE_BLOQUEO` | `Target` | `[data-testid="blocked-account-message"]` (fallback: `//p[contains(text(),'bloqueada')]`) | Account-blocked message |

**Rule**: No selector strings appear anywhere outside this class.  
**Selector confirmation**: exact values verified against running FrontendTicketing DOM at implementation time.

---

### 7. `Credenciales` (Value Object)

Immutable data carrier for login test data.

| Field | Type | Description |
|-------|------|-------------|
| email | `String` | User email/username |
| password | `String` | User password |

**Methods**:
- `Credenciales.validas() → Credenciales` — fixed valid credentials for CP-HU2-01 (from `serenity.conf` or environment variable)
- `Credenciales.invalidas() → Credenciales` — credentials with wrong password, same email as the UUID account
- `toJson() → String` — serializes to JSON body for registration API call

---

### 8. `CredencialesBuilder` (Utility)

Generates and stores credentials for test execution context.

| Method | Signature | Description |
|--------|-----------|-------------|
| `conEmailUUID()` | `static Credenciales` | Generates `Credenciales` with `UUID.randomUUID() + "@test.com"` email and fixed test password |
| `guardar(Credenciales)` | `static void` | Stores in `ThreadLocal<Credenciales>` for retrieval by Step Definitions |
| `obtener()` | `static Credenciales` | Retrieves stored credentials from `ThreadLocal` |
| `limpiar()` | `static void` | Called in `@After` to clear `ThreadLocal` and prevent memory leaks |

---

### 9. `HooksAutenticacion` (Cucumber Hooks)

Manages Actor lifecycle and scenario-specific setup.

| Hook | Scope | Responsibility |
|------|-------|----------------|
| `@Before` (untagged) | All HU2 scenarios | Initialize Actor with `BrowseTheWeb` ability; store in instance variable for Step Def injection |
| `@Before("@CP-HU2-09")` | CP-HU2-09 only | Generate UUID credentials, POST to backend registration endpoint, store via `CredencialesBuilder.guardar()` |
| `@After` (untagged) | All HU2 scenarios | Quit WebDriver; call `CredencialesBuilder.limpiar()` |

---

## Entity Relationships

```
HooksAutenticacion
  creates ──────────────────────► Actor
  creates (CP-HU2-09 only) ────► Credenciales (via CredencialesBuilder + HttpClient)

Actor
  attemptsTo ──────────────────► IniciarSesion (Task)
  should(seeThat(...)) ────────► UrlActual (Question)
                              ► TokenGuardado (Question)
                              ► MensajeDeBloqueoCuenta (Question)

IniciarSesion
  uses selectors from ─────────► LoginUi

TokenGuardado
  reads ───────────────────────► localStorage['auth_token']

MensajeDeBloqueoCuenta
  uses selector from ──────────► LoginUi.MENSAJE_BLOQUEO

SesionUsuarioStepDefinitions
  composes Tasks ──────────────► actor.attemptsTo(IniciarSesion.como(...)) × 3 for CP-HU2-09
  receives ────────────────────► Actor (via Cucumber PicoContainer / constructor injection)
  reads ───────────────────────► CredencialesBuilder.obtener() for CP-HU2-09 credentials
```

---

## State Transitions

### CP-HU2-01

```
[Login page] → IniciarSesion(valid) → [Home page "/"] + [localStorage auth_token = <token>]
```

### CP-HU2-09

```
[Account created with UUID email, attempt_count = 0]
  → IniciarSesion(invalid) × 1 → [Login page, error msg, attempt_count = 1]
  → IniciarSesion(invalid) × 2 → [Login page, error msg, attempt_count = 2]
  → IniciarSesion(invalid) × 3 → [Login page, BLOCKED MESSAGE visible, attempt_count = 3, account locked]
```

---

## Validation Rules

| Entity | Rule | Enforcement |
|--------|------|-------------|
| `IniciarSesion` | Must not contain conditional logic or call other Tasks | Code review (SC-004) |
| `LoginUi` | All selector strings MUST live here; none in Tasks/Steps | Code review |
| `CredencialesBuilder` | `ThreadLocal` MUST be cleared in `@After` | `HooksAutenticacion.@After` |
| `TokenGuardado` | Storage key is `"auth_token"` — constant, not a parameter | Static final in class |
| `Actor` | Fresh instance per scenario, never shared | `HooksAutenticacion.@Before` + `@After` |
