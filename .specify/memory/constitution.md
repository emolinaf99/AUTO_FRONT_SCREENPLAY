# AUTO_FRONT_SCREENPLAY Constitution

## Core Principles

### I. Spec-First
Toda funcionalidad comienza con un spec aprobado en `.specify/specs/`.
No se escribe código de producción sin spec + plan aprobados.
El spec define los escenarios observables en el sistema real; no se inventan flujos.

### II. Java + Serenity BDD
Stack único: Java 21, Serenity BDD 4.2.9, Selenium, JUnit 4, Cucumber.
`CucumberWithSerenity` es el runner obligatorio.
`serenity-gradle-plugin 4.2.9` genera el reporte agregado.

### III. Screenplay Pattern (NON-NEGOTIABLE)
Patrón obligatorio: Actors, Tasks, Actions, Questions con responsabilidad única (SRP).
`Target.the(...).locatedBy(...)` para todos los selectores — sin `@FindBy`, sin `driver.findElement`.
Tasks implementan `Performable`; Questions implementan `Question<T>`.
Los Steps (`@Step`) solo orquestan Tasks y Questions; cero lógica de UI directa.
Los escenarios de este proyecto DEBEN ser distintos a los de `AUTO_FRONT_POM_FACTORY`.

### IV. Código Limpio
Sin comentarios en código, sin lógica de negocio en el runner.
Configuración de URLs y propiedades en `serenity.conf`, no en Java.
Nomenclatura semántica en todos los artefactos.

### V. Escenarios Independientes
Cada escenario prepara sus propias precondiciones vía API antes de ejecutarse.
Ningún escenario depende del estado dejado por otro.
Ejecutar cualquier escenario en aislamiento produce PASS.

## Constraints Técnicos

- Target: `http://localhost:3000` (Frontend Next.js — TicketRush)
- Página de prueba: `/admin` — Panel de gestión de eventos
- Sin autenticación: la aplicación no tiene login
- Datos de prueba controlados por API antes de cada escenario
- `./gradlew test aggregate` es el comando de ejecución

## Quality Gates

- 2 escenarios Gherkin en 1 `.feature`, ambos con estado PASS en reporte Serenity
- Cada escenario es ejecutable de forma aislada
- Código sin comentarios ni variables no semánticas
- Reporte en `target/site/serenity/`

## Governance

Esta constitución prevalece sobre cualquier otra práctica.
Enmiendas requieren documentación, aprobación y plan de migración.
Toda PR/revisión debe verificar cumplimiento de los 5 principios.

**Version**: 1.0.0 | **Ratified**: 2026-03-20 | **Last Amended**: 2026-03-20
