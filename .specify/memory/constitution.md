# AUTO_FRONT_SCREENPLAY Constitution

## I. Stack y Herramientas (NO NEGOCIABLE)

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Java 21 |
| Framework de Automatización | Serenity BDD 4.2.0 |
| Runner de Pruebas | Cucumber 7.18.0 + JUnit 4.13.2 |
| Gestión de Dependencias | Gradle |
| Patrón UI | Screenplay (Actor / Task / Action / Question) |
| Librerías Screenplay | serenity-screenplay 4.2.0 + serenity-screenplay-webdriver 4.2.0 |
| Driver | Selenium WebDriver — Chrome |
| Reporte | Serenity single-page HTML |

No se agrega ninguna dependencia nueva sin justificación explícita. No se usa el patrón POM en este repositorio — ese patrón pertenece a AUTO_FRONT_POM_FACTORY.

---

## II. Patrón de Automatización (NO NEGOCIABLE)

**Screenplay — Actor / Task / Action / Question:**

```
Actor → attemptTo(Task) → Task usa Actions → Question verifica estado
```

**Reglas estrictas del patrón:**
- **Actor**: representa a un usuario con habilidades (`BrowseTheWeb`). No se comparte entre escenarios.
- **Task**: agrupa `Actions` para representar un objetivo de negocio. Una `Task` = una responsabilidad (SRP). Las Tasks NO llaman a otras Tasks.
- **Action**: interacción atómica con la UI (`Click`, `Enter`, `Navigate`). Reutilizable.
- **Question**: consulta el estado observable de la UI sin modificarlo. Retorna un valor para aserción.
- **UI (Target/PageElement)**: define los localizadores CSS/XPath centralizados. No se escriben selectores en Tasks ni Step Definitions.

---

## III. Arquitectura de Carpetas (NO NEGOCIABLE)

```
src/test/
├── java/com/ticketing/
│   ├── tasks/               ← Tasks: objetivos de negocio (SRP)
│   │   ├── IniciarSesion.java
│   │   ├── CerrarSesion.java
│   │   └── IntentarLoginConCuentaBloqueada.java
│   ├── questions/           ← Questions: consultas al estado de la UI
│   │   ├── EstaAutenticado.java
│   │   └── MensajeDeError.java
│   ├── ui/                  ← Localizadores centralizados (Target / PageElement)
│   │   ├── LoginUi.java
│   │   └── NavBarUi.java
│   ├── stepdefinitions/     ← Step Definitions por feature
│   │   ├── SesionUsuarioStepDefinitions.java
│   │   └── ConsultaReservaTicketsStepDefinitions.java
│   ├── runners/             ← CucumberTestRunner
│   │   └── CucumberTestRunner.java
│   └── hooks/               ← @Before / @After (setup Actor + teardown driver)
└── resources/
    ├── features/
    │   ├── consulta_y_reserva_tickets.feature   ← existente, NO modificar
    │   └── sesion_usuario.feature               ← nueva — feature de auth
    ├── serenity.conf
    └── logback-test.xml
```

---

## IV. Convenciones de Código

**Tasks:**
- Nombre: verbo de negocio en infinitivo → `IniciarSesion`, `CerrarSesion`, `IntentarLoginConCuentaBloqueada`
- Implementan `Performable` o extienden `Task`
- Método de fábrica estático: `IniciarSesion.como(email, password)`
- Una sola responsabilidad — si una Task hace dos cosas, debe dividirse

**Questions:**
- Nombre: sustantivo o pregunta → `EstaAutenticado`, `MensajeDeError`
- Implementan `Question<T>`
- Solo leen — nunca modifican estado de la UI

**UI (localizadores):**
- Nombre: `NombrePagina` + sufijo `Ui` → `LoginUi`, `NavBarUi`
- Constantes `Target` o `PageElement` con nombre semántico
- Sin selectores en Tasks, Actions ni Step Definitions

**Gherkin:**
- Escenarios en español, declarativos, enfocados en comportamiento de negocio
- Sin antipatrones: no describir clics ni selectores
- Cada escenario independiente — el Actor se inicializa en `@Before`

---

## V. Aplicación Bajo Prueba

| Servicio | URL |
|----------|-----|
| FrontendTicketing (Vue 3) | `http://localhost:5173` |

Configuración del driver en `serenity.conf`:
```
webdriver.driver = chrome
headless.mode = false
```

---

## VI. Scope de Esta Feature — Autenticación (HU2)

**Restricción crítica**: los escenarios de este repositorio NO pueden coincidir con los automatizados en AUTO_FRONT_POM_FACTORY. Los escenarios de registro (HU1) pertenecen al repo POM.

**Dentro del alcance de este repositorio:**

| CP | Descripción | Tipo | Prioridad |
|----|-------------|------|-----------|
| CP-HU2-01 | Login exitoso → token guardado + redirección a `/` | Positivo | Obligatorio |
| CP-HU2-09 | Tercer intento fallido → cuenta bloqueada + mensaje de bloqueo | Negativo | Obligatorio |
| CP-HU2-15 | Logout → token eliminado + redirección a vista pública | Positivo | Recomendado |
| CP-HU2-16 | Acceso a ruta protegida tras logout → redirige a `/login` | Negativo | Recomendado |
| CP-HU2-10 | Login con cuenta ya bloqueada + password correcta → sigue bloqueado | Negativo | Recomendado |

**Fuera del alcance de este repositorio:**
- Escenarios de registro (HU1) → AUTO_FRONT_POM_FACTORY
- Pruebas de API REST → AUTO_API_SCREENPLAY
- CP-HU2-08, CP-HU2-11, CP-HU2-12, CP-HU2-13, CP-HU2-14 → pruebas de backend/seguridad, no automatizables en UI E2E
- CP-HU2-02 (rol admin) → rol admin no existe en frontend v1

---

## VII. Reglas de Calidad (NO NEGOCIABLE)

- **SRP en Tasks**: una Task = una responsabilidad. Si una Task contiene más de un objetivo de negocio, debe dividirse.
- **Tasks no llaman Tasks**: las Tasks usan `Actions` de Serenity (`Click`, `Enter`, `Navigate`). Las composiciones de Tasks van en los Step Definitions, no en otras Tasks.
- **Independencia de escenarios**: el Actor se crea en `@Before` y se destruye en `@After`. Ningún escenario hereda estado del anterior.
- **Sin código comentado**: ausencia total de código comentado. Sin `//TODO` sin contexto ni código deshabilitado.
- **Nomenclatura semántica**: nombres de Tasks, Questions y UI que expresen intención de negocio, no detalles de implementación.
- **Localizadores centralizados**: todos los selectores CSS/XPath en clases `Ui`. Prohibido escribir selectores directamente en Tasks o Step Definitions.

---

## VIII. Definition of Done

Una tarea está completa cuando:

- El escenario Gherkin compila y ejecuta sin errores
- Cada Task tiene una única responsabilidad (SRP verificado)
- Los localizadores están en clases `Ui`, no dispersos en Tasks o Steps
- El escenario es independiente (Actor fresco en cada escenario)
- El reporte Serenity HTML se genera con el resultado del escenario
- Sin código comentado en ninguna clase
- `./gradlew clean test aggregate` pasa sin errores de compilación

---

## Governance

Esta constitución extiende el proyecto existente sin contradecirlo. El repositorio ya cubre la feature de consulta y reserva de tickets (`consulta_y_reserva_tickets.feature`) — esa feature se conserva intacta. La feature de autenticación se agrega como extensión independiente con escenarios distintos a los del repo POM.

**Version**: 1.0.0 | **Ratified**: 2026-04-08 | **Last Amended**: 2026-04-08
