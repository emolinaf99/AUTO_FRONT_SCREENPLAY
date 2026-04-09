# Quickstart: Screenplay Login & Account Block Automation — HU2

**Branch**: `001-screenplay-login-block`  
**Updated**: 2026-04-08

---

## Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| Java (JDK) | 21 | `java -version` must show 21 |
| Google Chrome | Latest stable | Must be installed and accessible in `PATH` |
| ChromeDriver | Matching Chrome version | Auto-managed by Serenity/WebDriverManager |
| FrontendTicketing | Running | Must be accessible at `http://localhost:5173` |
| AuthService | Running | Must be accessible at `http://localhost:8003`; the `@Before("@CP-HU2-09")` hook POSTs to `http://localhost:8003/api/auth/register` to create the UUID test account |
| Gradle | Wrapper included | Use `./gradlew` — do not install Gradle separately |

---

## Setup

### 1. Clone and confirm

```bash
git clone <repo-url>
cd SetPruebas/PatronScreenplay
git checkout 001-screenplay-login-block
```

### 2. Verify FrontendTicketing is running

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:5173
# Expected: 200
```

### 3. Supply valid credentials for CP-HU2-01 at runtime

Credentials are provided **exclusively via system properties** at test execution time. `serenity.conf` must NOT be modified to store credential values.

```bash
./gradlew clean test aggregate \
  -Dtest.credentials.email="usuario@ejemplo.com" \
  -Dtest.credentials.password="ContraseñaValida123"
```

The Step Definitions read credentials via `System.getProperty("test.credentials.email")` and `System.getProperty("test.credentials.password")`. If these properties are not set, the CP-HU2-01 scenario will fail with a `NullPointerException` in the credentials read — this is intentional and visible.

---

## Run the Full Suite

```bash
./gradlew clean test aggregate
```

This runs all scenarios in `src/test/resources/features/` and generates the Serenity HTML report.

---

## Run Specific Scenarios

### CP-HU2-01 only (positive login):
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-01"
```

### CP-HU2-09 only (account block):
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-09"
```

### All HU2 scenarios:
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@HU2"
```

---

## Run in Headless Mode (CI)

```bash
./gradlew clean test aggregate -Dheadless.mode=true
```

The `serenity.conf` default is `headless.mode = false` (Chrome headed). Override with `-Dheadless.mode=true` for pipelines without a graphical environment.

---

## View Reports

After the test run, open the Serenity single-page HTML report:

```bash
# Linux
xdg-open target/site/serenity/index.html

# Or navigate to:
# target/site/serenity/index.html
```

---

## Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| `Connection refused` on `localhost:5173` | FrontendTicketing not running | Start the frontend app before running tests |
| CP-HU2-09 hook fails with `Connection refused` on port 8003 | AuthService not running | Start the AuthService backend before running tests; it must be accessible at `http://localhost:8003` |
| `NoSuchElementException` on login form | Selectors in `LoginUi.java` don't match current DOM | Inspect running app at `localhost:5173/login` and update selectors in `LoginUi.java` |
| `SessionNotCreatedException` | ChromeDriver version mismatch | Serenity uses WebDriverManager for auto-management; ensure Chrome is up to date |
| Token not found after login | `auth_token` key changed in `authStore.ts` | Verify key in browser DevTools → Application → Local Storage; update `TokenGuardado.STORAGE_KEY` constant |

---

## File Reference

| File | Purpose |
|------|---------|
| `src/test/resources/features/sesion_usuario.feature` | Gherkin scenarios (CP-HU2-01, CP-HU2-09) |
| `src/test/java/com/ticketing/tasks/IniciarSesion.java` | Login attempt Task |
| `src/test/java/com/ticketing/questions/UrlActual.java` | URL state Question |
| `src/test/java/com/ticketing/questions/TokenGuardado.java` | localStorage token Question |
| `src/test/java/com/ticketing/questions/MensajeDeBloqueoCuenta.java` | Block message visibility Question |
| `src/test/java/com/ticketing/ui/LoginUi.java` | Centralized login page selectors |
| `src/test/java/com/ticketing/stepdefinitions/SesionUsuarioStepDefinitions.java` | Step definitions for HU2 |
| `src/test/java/com/ticketing/hooks/HooksAutenticacion.java` | Actor lifecycle + UUID account creation hook |
| `src/test/java/com/ticketing/util/CredencialesBuilder.java` | UUID credential factory + ThreadLocal context |
| `src/test/resources/serenity.conf` | WebDriver config (chrome, headless.mode) — do not add credential values here |
