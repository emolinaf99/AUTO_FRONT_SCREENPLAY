# Contract: sesion_usuario.feature — HU2 Gherkin Scenarios

**Phase**: 1 — Design  
**Branch**: `001-screenplay-login-block`  
**Date**: 2026-04-08  
**Target file**: `src/test/resources/features/sesion_usuario.feature`

This document defines the BDD contract for HU2. The `.feature` file is the binding interface between the business requirement and the automation implementation. Any change to scenario wording must be agreed between QA and the product owner; renaming a step string breaks the step definition binding.

---

## Feature Contract

```gherkin
# language: es
@HU2
Característica: Sesión de usuario — Login y bloqueo de cuenta
  Como usuario registrado de FrontendTicketing
  Quiero iniciar sesión con mis credenciales
  Para acceder a las funcionalidades de la aplicación

  @CP-HU2-01
  Escenario: Login exitoso con credenciales válidas guarda token JWT y redirige al inicio
    Dado que el usuario está en la página de login de FrontendTicketing
    Cuando inicia sesión con credenciales válidas
    Entonces es redirigido a la página de inicio
    Y el token JWT queda guardado en el almacenamiento local

  @CP-HU2-09
  Escenario: Tercer intento fallido consecutivo bloquea la cuenta y muestra mensaje de bloqueo
    Dado que el usuario está en la página de login de FrontendTicketing
    Cuando intenta iniciar sesión con credenciales incorrectas por primera vez
    Y intenta iniciar sesión con credenciales incorrectas por segunda vez
    Y intenta iniciar sesión con credenciales incorrectas por tercera vez
    Entonces la aplicación muestra el mensaje de cuenta bloqueada
```

---

## Step Binding Contract

Each step string maps to exactly one method in `SesionUsuarioStepDefinitions.java`. Changing a step string here requires changing the corresponding annotation in the Step Definition.

| Step | Regex / String | Method |
|------|----------------|--------|
| `Dado que el usuario está en la página de login de FrontendTicketing` | Exact match | `elUsuarioEstaEnLaPaginaDeLogin()` |
| `Cuando inicia sesión con credenciales válidas` | Exact match | `iniciaSesionConCredencialesValidas()` |
| `Entonces es redirigido a la página de inicio` | Exact match | `esRedirigidoAlInicio()` |
| `Y el token JWT queda guardado en el almacenamiento local` | Exact match | `elTokenJwtQuedaGuardado()` |
| `Cuando intenta iniciar sesión con credenciales incorrectas por primera vez` | Exact match | `intentarLoginInvalido()` |
| `Y intenta iniciar sesión con credenciales incorrectas por segunda vez` | Exact match | `intentarLoginInvalido()` (same method, reused via `@Y`) |
| `Y intenta iniciar sesión con credenciales incorrectas por tercera vez` | Exact match | `intentarLoginInvalido()` (same method, reused via `@Y`) |
| `Entonces la aplicación muestra el mensaje de cuenta bloqueada` | Exact match | `laAplicacionMuestraElMensajeDeBloqueoCuenta()` |

---

## Tag Contract

| Tag | Scope | Purpose |
|-----|-------|---------|
| `@HU2` | Feature | Groups all HU2 authentication scenarios for selective run |
| `@CP-HU2-01` | Escenario | Scopes positive login scenario; triggers no special `@Before` hooks |
| `@CP-HU2-09` | Escenario | Scopes block scenario; triggers `@Before("@CP-HU2-09")` UUID account creation hook |

---

## Out-of-Scope Scenarios (explicit exclusions)

The following scenarios are intentionally absent from this feature file:

| Scenario | Owner | Reason |
|----------|-------|--------|
| Registro de nuevo usuario (HU1) | AUTO_FRONT_POM_FACTORY | Prohibited by FR-006 and constitution |
| Validación de campos vacíos en login | AUTO_FRONT_POM_FACTORY | Client-side form validation; out of scope per clarification Q4 |
| Logout (CP-HU2-15) | Future iteration | Recommended but not in mandatory scope for this plan |
| Acceso a ruta protegida tras logout (CP-HU2-16) | Future iteration | Recommended but not in mandatory scope |
| CP-HU2-10 (login con cuenta ya bloqueada) | Future iteration | Recommended extension |
| HTTP 423 network-layer assertion | AUTO_API_SCREENPLAY | Prohibited by clarification Q1; out of UI scope |
