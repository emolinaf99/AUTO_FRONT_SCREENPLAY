# Implementation Plan: Front-End Automation Admin Panel Flow

**Branch**: `001-screenplay` | **Date**: 2026-03-20 | **Spec**: `.specify/specs/001-screenplay/spec.md`
**App context**: `.specify/memory/app-context.md`

## Summary

Implementar un proyecto de automatización Front-End sobre el flujo de administración de eventos
usando Serenity BDD + Selenium con patrón Screenplay (Actors/Tasks/Questions/SRP).
La automatización cubrirá exactamente 2 escenarios Gherkin dentro de una sola feature:

- **US1 (positivo)**: navegar a `/admin` y confirmar heading "Gestión de Eventos" + botón "Crear Evento" visible
- **US2 (negativo)**: abrir el formulario de creación, enviar sin nombre, verificar mensaje de obligatoriedad y persistencia del dialog

Cada escenario preparará su estado previo por API contra el CRUD Service antes de navegar al frontend,
para cumplir la independencia exigida por la constitución.

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Serenity BDD 4.2.9, Selenium, Serenity REST Assured 4.2.9, JUnit 4.13.2, Cucumber
**Storage**: N/A
**Testing**: JUnit 4 + Cucumber (`CucumberWithSerenity`) + Serenity aggregate reports
**Target Platform**: Chrome sobre `http://localhost:3000` (Frontend Next.js)
**Project Type**: Test automation — Front-End UI con Screenplay
**Constraints**: 2 escenarios en 1 `.feature`, preparación previa por API obligatoria, `Target.the(...).locatedBy(...)` obligatorio, sin `@FindBy`, sin código comentado
**Scale/Scope**: 1 UI Map (`AdminPage`), 4 Tasks, 3 Questions, 1 Steps class, 1 Runner

## Constitution Check

- ✅ I. Spec-First: `spec.md` aprobado y alineado al sistema real antes de crear código
- ✅ II. Java + Serenity BDD: stack confirmado
- ✅ III. Screenplay Pattern: Tasks/Questions/SRP, sin `@FindBy`, sin lógica UI en Steps
- ✅ IV. Código Limpio: configuración declarativa, sin lógica de negocio en runner
- ✅ V. Escenarios Independientes: 2 escenarios en una sola feature, ejecutables de forma aislada

## Project Structure

```text
src/
  test/
    java/
      runners/
        CucumberTestRunner.java
      steps/
        AdminPanelSteps.java
      screenplay/
        ui/
          AdminPage.java
        tasks/
          PrepareAdminState.java
          NavigateToAdmin.java
          OpenCreateEventForm.java
          SubmitEventFormWithoutName.java
        questions/
          TheAdminPanel.java
          TheCreateEventDialog.java
          TheValidationError.java
    resources/
      features/
        admin/
          gestion-eventos.feature
      serenity.conf

build.gradle
```

## build.gradle

```groovy
plugins {
    id 'java'
    id 'net.serenity-bdd.serenity-gradle-plugin' version '4.2.9'
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'net.serenity-bdd:serenity-core:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-junit:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-cucumber:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-screenplay:4.2.9'
    testImplementation 'net.serenity-bdd:serenity-screenplay-webdriver:4.2.9'
  testImplementation 'net.serenity-bdd:serenity-rest-assured:4.2.9'
    testImplementation 'junit:junit:4.13.2'
    testImplementation 'org.assertj:assertj-core:3.25.3'
}

test {
    testLogging.showStandardStreams = true
    systemProperties System.getProperties()
}

gradle.startParameter.continueOnFailure = true
test.finalizedBy(aggregate)
```

## serenity.conf

```hocon
webdriver {
    driver = chrome
    autodownload = true
    base.url = "http://localhost:3000"
}

api {
  crud.base.url = "http://localhost:8002"
}

headless.mode = false

serenity {
    project.name = "AUTO_FRONT_SCREENPLAY"
    test.root = "runners"
}
```

## Feature File

**Archivo**: `src/test/resources/features/admin/gestion-eventos.feature`

```gherkin
#language: es
Característica: Gestión de eventos en panel de administración
  Como administrador del sistema
  Quiero gestionar los eventos desde el panel de administración
  Para verificar la disponibilidad de la interfaz y las validaciones del formulario

  @happy-path @critico
  Escenario: El administrador visualiza el panel de gestión de eventos
    Dado que el administrador navega al panel de administración
    Entonces el sistema muestra el heading Gestión de Eventos
    Y el botón Crear Evento está disponible en la interfaz

  @error-path
  Escenario: El sistema bloquea la creación de evento cuando falta el nombre
    Dado que el administrador se encuentra en el panel de administración
    Y el administrador abre el formulario de creación de evento
    Cuando intenta crear un evento sin ingresar el nombre
    Entonces el sistema muestra un mensaje de obligatoriedad para el campo Nombre
    Y el administrador permanece en el formulario de creación
```

## Estrategia de Precondiciones por API

- Antes de cada escenario, el actor ejecuta `PrepareAdminState` contra el CRUD Service configurado en `serenity.conf`.
- `PrepareAdminState` deja el módulo admin en estado conocido y verificable antes de abrir `/admin`.
- La implementación no debe codificar URLs de backend en Java; debe leer la configuración declarativa.

## Screenplay Responsibilities

| Artefacto | Tipo | Responsabilidad única |
|---|---|---|
| `AdminPage` | UI Map | `Target` definitions: heading, botón "Crear Evento", dialog de creación, campo Nombre, botón submit, error validación |
| `PrepareAdminState` | Task | Restablece por API el estado conocido del panel admin antes de cada escenario |
| `NavigateToAdmin` | Task | `actor.attemptsTo(Open.browserOn("/admin"))` |
| `OpenCreateEventForm` | Task | `actor.attemptsTo(Click.on(AdminPage.CREAR_EVENTO_BUTTON))` |
| `SubmitEventFormWithoutName` | Task | Limpiar campo nombre + click en botón submit del form |
| `TheAdminPanel` | Question | Verifica visibilidad del heading y del botón "Crear Evento" |
| `TheValidationError` | Question | Verifica mensaje visible asociado al campo Nombre indicando obligatoriedad |
| `TheCreateEventDialog` | Question | Verifica que el dialog, el campo Nombre y el botón submit siguen visibles |
| `AdminPanelSteps` | Steps | Orquesta Tasks y Questions con `@Step` annotations y usa driver administrado por Serenity |

## Complexity Tracking

| Violación | Por qué se necesita | Alternativa rechazada |
|---|---|---|
| N/A | Sin violaciones constitucionales | N/A |
