# Research: Screenplay Login & Account Block Automation — HU2

**Phase**: 0 — Resolve all architectural unknowns before design  
**Branch**: `001-screenplay-login-block`  
**Date**: 2026-04-08

---

## R-01: Task Internal Composition with Serenity Actions

**Unknown**: How does `IniciarSesion` compose Serenity primitive Actions while respecting SRP?

**Decision**: `IniciarSesion` implements `Performable` and uses only Serenity-provided `Actions` (`Navigate.to()`, `Enter.theValue().into()`, `Click.on()`). It accepts `email` and `password` via a static factory method. It does NOT delegate to other Tasks — Actions are the atomic unit, Tasks are the business unit.

```java
// Structural blueprint (not final code)
public class IniciarSesion implements Task {
    private final String email;
    private final String password;

    // Static factory — SRP-compliant, one business objective
    public static IniciarSesion como(String email, String password) { ... }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Navigate.to("http://localhost:5173/login"),
            Enter.theValue(email).into(LoginUi.CAMPO_EMAIL),
            Enter.theValue(password).into(LoginUi.CAMPO_PASSWORD),
            Click.on(LoginUi.BOTON_INGRESAR)
        );
    }
}
```

**Rationale**: The `Navigate.to()` is included inside the Task because a login attempt always starts from the login page — this is the full business objective. The Step Definition for CP-HU2-09 calls `IniciarSesion.como(email, wrongPassword)` three times sequentially; this is composition at the Step Definition level, not Task-to-Task delegation.

**Alternatives considered**:
- Separate `NavegaAlLogin` + `IngresarCredenciales` + `EnviarFormulario` Tasks: rejected — over-fragments the business objective and creates three-Task composition in Step Defs for every scenario, violating the spirit of SRP (business responsibility ≠ UI micro-action).
- Single Task with boolean "navigate first" flag: rejected — conditional logic in a Task is a code smell and obscures intent.

---

## R-02: Reading `localStorage` in Serenity Screenplay

**Unknown**: How does `TokenGuardado` Question access `window.localStorage.getItem('auth_token')` from within a Serenity test?

**Decision**: Use `BrowseTheWeb.as(actor).getDriver().executeScript(...)` which exposes the underlying Selenium `JavascriptExecutor`. Return the result as `String`; a non-null, non-empty value means the token was stored.

```java
// Structural blueprint
public class TokenGuardado implements Question<String> {
    public static TokenGuardado enLocalStorage() { return new TokenGuardado(); }

    @Override
    public String answeredBy(Actor actor) {
        return (String) BrowseTheWeb.as(actor)
            .getDriver()
            .executeScript("return window.localStorage.getItem('auth_token');");
    }
}
```

**Assertion in Step Def**:
```java
actor.should(seeThat(TokenGuardado.enLocalStorage(), is(notNullValue())));
actor.should(seeThat(TokenGuardado.enLocalStorage(), not(emptyString())));
```

**Rationale**: JavaScript execution via WebDriver is the standard cross-browser mechanism to read `localStorage`. No additional library is needed. The key `auth_token` is canonical per `authStore.ts`; it is a constant in the Question, not a parameter, to keep the API minimal.

**Alternatives considered**:
- Serenity `TheWebPage.currentUrl()` / built-in Page state: does not expose storage, only DOM/URL.
- Storing token via network interception: rejected (violates UI-only scope per clarification Q1).

---

## R-03: Asserting Current URL

**Unknown**: How does `UrlActual` Question retrieve the browser's current URL?

**Decision**: Use `BrowseTheWeb.as(actor).getDriver().getCurrentUrl()`. Compare with `containsString("/")` or `equalTo("http://localhost:5173/")` in the assertion.

```java
public class UrlActual implements Question<String> {
    public static UrlActual delNavegador() { return new UrlActual(); }

    @Override
    public String answeredBy(Actor actor) {
        return BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
    }
}
```

**Rationale**: `getCurrentUrl()` is the simplest reliable mechanism in Selenium/WebDriver. Serenity's own `CurrentPage.url()` delegates to the same call internally. Using `BrowseTheWeb.as(actor)` is the idiomatic Screenplay way to access the driver without injecting it directly.

**Alternatives considered**:
- `TheWebPage.currentUrl()` (Serenity static target): functionally equivalent but less explicit about which actor's browser session is being queried in multi-actor scenarios.

---

## R-04: UUID Account Creation for CP-HU2-09

**Unknown**: How does CP-HU2-09 get a fresh account (zero failed-attempt counter) without UI registration (POM) and without a fixed pre-seeded account?

**Decision**: A tagged `@Before("@CP-HU2-09")` hook in `HooksAutenticacion.java` uses Java 21's built-in `java.net.http.HttpClient` to POST to the backend registration endpoint, creating a new user with a UUID-generated email. The response credentials are stored in a `ThreadLocal<Credenciales>` (`CredencialesBuilder.contextoActual()`) which the Step Definition reads.

```java
// Structural blueprint — HooksAutenticacion.java
@Before("@CP-HU2-09")
public void crearCuentaFrescaUUID() {
    Credenciales creds = CredencialesBuilder.conEmailUUID();
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8003/api/auth/register"))  // AuthService port — NOT the Vue 3 frontend port 5173
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(creds.toJson()))
        .build();
    client.send(request, HttpResponse.BodyHandlers.ofString());
    CredencialesBuilder.guardar(creds);
}
```

**Rationale**: Java 21 `HttpClient` is JDK-native — no new `build.gradle` dependency. The UUID email guarantees a zero failed-attempt counter without any reset endpoint. Storing in `ThreadLocal` ensures thread safety in parallel execution. This is the identical uniqueness pattern as CP-HU1-08 in AUTO_FRONT_POM_FACTORY. The registration endpoint lives on the AuthService at **port 8003**, which is a separate process from the FrontendTicketing Vue 3 app running on port 5173.

**Alternatives considered**:
- Fixed pre-seeded account with `@Before` API reset: requires a reset/unlock backend endpoint; adds dependency on a non-public admin API that may not exist.
- RestAssured for HTTP call: would require a new `build.gradle` dependency (violates constraint).
- `@BeforeScenario` registering via UI (Screenplay tasks): would replicate AUTO_FRONT_POM_FACTORY registration flow — explicitly prohibited.

---

## R-05: Gherkin Tag Strategy

**Unknown**: How should CP-HU2-01 and CP-HU2-09 be tagged to support selective execution and the `@Before` hook scope?

**Decision**:
- Feature-level tag: `@HU2`
- Scenario-level tags: `@CP-HU2-01` and `@CP-HU2-09`
- The `@Before("@CP-HU2-09")` hook fires only for the block scenario

Selective execution examples:
```bash
./gradlew clean test aggregate -Dcucumber.filter.tags="@CP-HU2-01"
./gradlew clean test aggregate -Dcucumber.filter.tags="@HU2"
```

**Rationale**: Tag-scoped hooks are the idiomatic Cucumber mechanism to run setup only for relevant scenarios. This avoids creating a fresh UUID account on every scenario unnecessarily.

---

## R-06: Blocked-Account Message Selector Strategy

**Unknown**: What CSS selector or XPath strategy should `LoginUi` use for the blocked-account message?

**Decision**: Use a `data-testid` attribute if available in the FrontendTicketing Vue 3 DOM (e.g., `[data-testid="blocked-account-message"]`), falling back to a text-content XPath (`//p[contains(text(),'bloqueada')]`) if no test ID exists. The selector is a constant in `LoginUi` — the Question only asks "is it visible?", it does not know the selector.

**Rationale**: `data-testid` attributes are resilient to CSS class refactoring. If the frontend does not provide them, XPath by text content is the next most resilient option for Spanish-language Vue 3 apps. The selector must be confirmed against the running FrontendTicketing DOM before implementation.

**Status**: Selector strategy confirmed in principle; exact attribute value to be verified against running app at implementation time. This is NOT a blocker for spec/plan completion.

---

## Resolution Summary

| ID | Topic | Status | Key Decision |
|----|-------|--------|-------------|
| R-01 | Task internal composition | ✅ Resolved | `IniciarSesion` uses Actions; composition in Step Defs |
| R-02 | `localStorage` read | ✅ Resolved | `executeScript` via `BrowseTheWeb.as(actor).getDriver()` |
| R-03 | URL assertion | ✅ Resolved | `getCurrentUrl()` via `BrowseTheWeb.as(actor).getDriver()` |
| R-04 | UUID account for CP-HU2-09 | ✅ Resolved | Java 21 `HttpClient` in tagged `@Before` hook |
| R-05 | Gherkin tag strategy | ✅ Resolved | `@HU2` feature + `@CP-HU2-XX` scenario tags |
| R-06 | Blocked-message selector | ✅ Resolved (strategy) | `data-testid` preferred; fallback XPath by text |

**All research items resolved. Phase 1 design can proceed.**
