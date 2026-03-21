# Tasks: Front-End Automation Admin Panel Flow

**Input**: Design documents from `.specify/specs/001-screenplay/`
**Prerequisites**: plan.md, spec.md

**Tests**: Este proyecto implementa automatización E2E con Serenity BDD; los escenarios Gherkin, runner y validaciones forman parte del entregable.

**Organization**: Las tareas se agrupan por historia de usuario para permitir implementación y validación independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicializar la estructura base del proyecto de automatización.

- [X] T001 Crear la estructura base de paquetes y recursos en src/test/java/ y src/test/resources/features/admin/
- [X] T002 [P] Configurar dependencias y plugin de Serenity en build.gradle
- [X] T003 [P] Configurar navegador, URL base y URL del CRUD Service en src/test/resources/serenity.conf
- [X] T004 Crear el archivo único de escenarios en src/test/resources/features/admin/gestion-eventos.feature

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implementar la infraestructura compartida que bloquea todas las historias.

**⚠️ CRITICAL**: Ninguna historia de usuario debe empezar antes de completar esta fase.

- [X] T005 Crear el runner Cucumber con Serenity en src/test/java/runners/CucumberTestRunner.java
- [X] T006 [P] Crear el mapa UI compartido con `Target.the(...).locatedBy(...)` en src/test/java/screenplay/ui/AdminPage.java
- [X] T007 [P] Implementar la preparación de estado por API en src/test/java/screenplay/tasks/PrepareAdminState.java
- [X] T008 Crear la configuración compartida de actor, `@Managed` y `@Before` en src/test/java/steps/AdminPanelSteps.java

**Checkpoint**: Base lista; las historias de usuario pueden implementarse y validarse de forma independiente.

---

## Phase 3: User Story 1 - Visualizar el panel de administración de eventos (Priority: P1) 🎯 MVP

**Goal**: Permitir que el actor prepare el estado, navegue a `/admin` y confirme que el panel muestra el heading y el botón principal.

**Independent Test**: Ejecutar solo `@happy-path` y comprobar PASS con evidencia en `target/site/serenity/index.html`.

### Implementation for User Story 1

- [X] T009 [P] [US1] Implementar la navegación al panel admin en src/test/java/screenplay/tasks/NavigateToAdmin.java
- [X] T010 [P] [US1] Implementar la pregunta de visibilidad del panel en src/test/java/screenplay/questions/TheAdminPanel.java
- [X] T011 [US1] Completar el escenario `@happy-path` en src/test/resources/features/admin/gestion-eventos.feature
- [X] T012 [US1] Implementar los steps de US1 con `@Step`, preparación API y aserciones en src/test/java/steps/AdminPanelSteps.java
- [X] T013 [US1] Validar la ejecución aislada de US1 con evidencia agregada en target/site/serenity/index.html

**Checkpoint**: US1 debe quedar ejecutable y verificable sin depender de US2.

---

## Phase 4: User Story 2 - Bloquear creación de evento cuando falta el nombre (Priority: P2)

**Goal**: Validar que el formulario de creación muestre obligatoriedad del campo Nombre y mantenga visible el diálogo tras el envío inválido.

**Independent Test**: Ejecutar solo `@error-path` y comprobar PASS con evidencia del mensaje de validación y del diálogo abierto en `target/site/serenity/index.html`.

### Implementation for User Story 2

- [X] T014 [P] [US2] Implementar la apertura del formulario de creación en src/test/java/screenplay/tasks/OpenCreateEventForm.java
- [X] T015 [P] [US2] Implementar el envío del formulario sin nombre en src/test/java/screenplay/tasks/SubmitEventFormWithoutName.java
- [X] T016 [P] [US2] Implementar la validación visible del campo Nombre en src/test/java/screenplay/questions/TheValidationError.java
- [X] T017 [P] [US2] Implementar la verificación de persistencia del diálogo en src/test/java/screenplay/questions/TheCreateEventDialog.java
- [X] T018 [US2] Completar el escenario `@error-path` en src/test/resources/features/admin/gestion-eventos.feature
- [X] T019 [US2] Implementar los steps de US2 con doble aserción de validación y diálogo abierto en src/test/java/steps/AdminPanelSteps.java
- [X] T020 [US2] Validar la ejecución aislada de US2 con evidencia agregada en target/site/serenity/index.html

**Checkpoint**: US2 debe quedar ejecutable de forma aislada, con preparación previa por API y sin depender de US1.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Cerrar ajustes transversales, calidad y validación final.

- [X] T021 [P] Confirmar selectores reales, texto observable y estabilidad del mapa UI en src/test/java/screenplay/ui/AdminPage.java
- [X] T022 Ejecutar la suite completa y verificar el agregado final en target/site/serenity/index.html
- [X] T023 [P] Revisar ausencia de comentarios, nombres semánticos y URLs hardcodeadas en src/test/java/ y src/test/resources/serenity.conf
- [X] T024 [P] Verificar que no exista referencia al flujo `/buy` en src/test/resources/features/admin/gestion-eventos.feature y src/test/java/steps/AdminPanelSteps.java
- [X] T025 Confirmar que la preparación por API ocurre antes de navegar al frontend en src/test/java/steps/AdminPanelSteps.java y target/site/serenity/index.html

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1: Setup** → sin dependencias.
- **Phase 2: Foundational** → depende de Phase 1 y bloquea todas las historias.
- **Phase 3: US1** → depende de Phase 2.
- **Phase 4: US2** → depende de Phase 2; no depende funcionalmente de US1.
- **Phase 5: Polish** → depende de las historias que se quieran entregar.

### User Story Dependencies

- **US1 (P1)**: comienza después de Foundational y define el MVP.
- **US2 (P2)**: comienza después de Foundational, reutiliza la base compartida y debe seguir siendo independiente de US1.

### Dependency Graph

- `Setup -> Foundational -> US1 -> Polish`
- `Setup -> Foundational -> US2 -> Polish`

### Within Each User Story

- Implementar primero los artefactos Screenplay del story.
- Completar después el escenario Gherkin correspondiente.
- Integrar al final los steps y validar el story en aislamiento.

### Parallel Opportunities

- En **Setup** pueden avanzar en paralelo T002 y T003.
- En **Foundational** pueden avanzar en paralelo T006 y T007.
- En **US1** pueden avanzar en paralelo T009 y T010.
- En **US2** pueden avanzar en paralelo T014, T015, T016 y T017.
- En **Polish** pueden avanzar en paralelo T021, T023 y T024.

---

## Parallel Example: User Story 1

- T009 [US1] Implementar la navegación al panel admin en src/test/java/screenplay/tasks/NavigateToAdmin.java
- T010 [US1] Implementar la pregunta de visibilidad del panel en src/test/java/screenplay/questions/TheAdminPanel.java

## Parallel Example: User Story 2

- T014 [US2] Implementar la apertura del formulario de creación en src/test/java/screenplay/tasks/OpenCreateEventForm.java
- T015 [US2] Implementar el envío del formulario sin nombre en src/test/java/screenplay/tasks/SubmitEventFormWithoutName.java
- T016 [US2] Implementar la validación visible del campo Nombre en src/test/java/screenplay/questions/TheValidationError.java
- T017 [US2] Implementar la verificación de persistencia del diálogo en src/test/java/screenplay/questions/TheCreateEventDialog.java

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Setup.
2. Completar Foundational.
3. Completar US1.
4. Validar US1 en aislamiento con Serenity.
5. Detenerse para demo o revisión del MVP.

### Incremental Delivery

1. Setup + Foundational dejan la base lista.
2. Agregar US1 y validar en aislamiento.
3. Agregar US2 y validar en aislamiento.
4. Ejecutar Polish y validación agregada final.

### Parallel Team Strategy

1. El equipo completa Setup + Foundational.
2. Después:
	- Persona A: US1.
	- Persona B: US2.
3. Integrar y cerrar con Polish.

---

## Notes

- Todas las tareas siguen el formato obligatorio de checklist con ID, marcador `[P]` opcional y etiqueta `[US#]` cuando aplica.
- Cada historia puede probarse por separado con sus tags de Cucumber.
- La preparación de estado por API es obligatoria antes de abrir el frontend.
- `serenity.conf` es la única fuente permitida para URLs configurables.
