# Feature Specification: Front-End Automation Admin Panel Flow

**Feature Branch**: `001-screenplay`
**Created**: 2026-03-20
**Status**: Approved
**Input**: Sistema real TicketRush — flujo de administración de eventos en `/admin`
**Contexto de la app**: `.specify/memory/app-context.md`

## Entorno de Ejecución

| Dato | Valor | Fuente |
|---|---|---|
| URL base del frontend | `http://localhost:3000` | Frontend Next.js inspeccionado |
| URL base de preparación API | `http://localhost:8002` | CRUD Service documentado en app context |
| Página de administración | `/admin` | `frontend/app/admin/page.tsx` |
| Heading del panel | `Gestión de Eventos` | Observable en la UI |
| Botón de creación | `Crear Evento` | Observable en la UI |
| Campo requerido en form | `Nombre` (del evento) | Dialog de creación observable |
| Validación negativa | Mensaje visible asociado al campo Nombre indicando obligatoriedad | Observable en la UI |

## Precondiciones de Ejecución

- Antes de cada escenario se DEBE preparar por API el estado del módulo admin contra el CRUD Service en `http://localhost:8002`.
- La preparación DEBE dejar el catálogo de eventos en un estado conocido e independiente del escenario anterior.
- La automatización DEBE navegar al frontend solo después de recibir confirmación exitosa de la preparación por API.

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visualizar el panel de administración de eventos (Priority: P1)

Como administrador del sistema,
quiero navegar al panel de gestión de eventos,
para visualizar el heading "Gestión de Eventos" y la interfaz de administración disponible.

**Why this priority**: Es el punto de entrada al flujo de administración observable.
Sin confirmar que el panel carga correctamente, ningún otro flujo de admin puede validarse.

**Independent Test**: Puede verificarse de forma aislada preparando el estado por API,
navegando a `/admin` y confirmando que el heading "Gestión de Eventos" es visible en la pantalla.

**Acceptance Scenarios**:

1. **Given** el usuario navega al panel de administración,
   **Then** el sistema muestra el heading "Gestión de Eventos",
   **And** el botón "Crear Evento" está disponible en la interfaz.

---

### User Story 2 - Bloquear creación de evento cuando falta el nombre (Priority: P2)

Como administrador del sistema,
quiero recibir una validación cuando intento crear un evento sin nombre,
para corregir el formulario antes de enviar la solicitud.

**Why this priority**: Es la validación negativa observable más directa en el flujo de administración.
Complementa el flujo positivo y verifica que el formulario protege la integridad de los datos.

**Independent Test**: Puede verificarse de forma aislada preparando el estado por API,
navegando a `/admin`, haciendo clic en "Crear Evento", dejando el campo Nombre vacío,
enviando el formulario y confirmando que aparece un mensaje visible asociado al campo Nombre
indicando que es obligatorio, mientras el dialog continúa visible con el campo Nombre y el botón de envío presentes.

**Acceptance Scenarios**:

1. **Given** el usuario se encuentra en el panel de administración,
   **And** el usuario abre el formulario de creación de evento,
   **When** intenta crear un evento sin ingresar el nombre,
    **Then** el sistema muestra un mensaje visible asociado al campo Nombre indicando que es obligatorio,
    **And** el usuario permanece en el formulario con el dialog de creación aún visible.

---

### Edge Cases — Fuera de Alcance

Los siguientes casos quedan **explícitamente fuera del alcance** de este proyecto:

- Creación exitosa de evento con todos los campos completos (flujo post-dialog, no observable en spec actual).
- Validaciones de fecha o ubicación en el formulario de creación.
- Edición o eliminación de eventos desde el panel de admin.
- Flujos de autenticación o login, ya que no existen en la aplicación.
- Flujos de compra pública (`/buy`), cubiertos por `AUTO_FRONT_POM_FACTORY`.

---

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE mostrar el heading "Gestión de Eventos" al navegar a `/admin`.
- **FR-002**: El sistema DEBE mostrar el botón "Crear Evento" en el panel de administración.
- **FR-003**: El sistema DEBE abrir un dialog/modal de creación al hacer clic en "Crear Evento".
- **FR-004**: El sistema DEBE bloquear el envío y mostrar un mensaje visible asociado al campo Nombre indicando que es obligatorio cuando el campo Nombre está vacío.
- **FR-005**: El sistema DEBE mantener el dialog abierto cuando la validación falla, conservando visibles el campo Nombre y el botón de envío.
- **FR-006**: Los escenarios DEBEN ser completamente independientes entre sí, preparando su estado previo por API antes de abrir el frontend.
- **FR-007**: Ambos escenarios DEBEN residir en un único archivo `.feature`.
- **FR-008**: Los selectores DEBEN usar `Target.the(...).locatedBy(...)` — sin `@FindBy`.
- **FR-009**: Los Steps DEBEN llevar anotación `@Step` con descripción en lenguaje de negocio.
- **FR-010**: La configuración de URLs DEBE residir en `serenity.conf`, no en el código.
- **FR-011**: Los escenarios DEBEN ser distintos a los de `AUTO_FRONT_POM_FACTORY` (no flujo `/buy`).

### Key Entities

- **AdminPage** (UI Map): `Target` definitions para heading, botón "Crear Evento", campo Nombre, error de validación.
- **PrepareAdminState** (Task): Restablece por API el estado conocido del módulo admin antes de cada escenario.
- **NavigateToAdmin** (Task): Instruye al Actor a navegar a `/admin`.
- **OpenCreateEventForm** (Task): Instruye al Actor a hacer clic en "Crear Evento".
- **SubmitEventFormWithoutName** (Task): Instruye al Actor a enviar el formulario sin nombre.
- **TheAdminPanel** (Question): Verifica visibilidad del heading y del botón en el panel.
- **TheValidationError** (Question): Verifica presencia del error de validación.
- **TheCreateEventDialog** (Question): Verifica que el dialog de creación continúa visible después de la validación fallida.
- **AdminPanelSteps**: Orquesta Tasks y Questions; no contiene lógica de UI directa.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Los 2 escenarios Gherkin se ejecutan sin errores con `./gradlew test aggregate`.
- **SC-002**: El reporte Serenity en `target/site/serenity/` muestra ambos escenarios con estado PASS.
- **SC-003**: US1 confirma la visibilidad del heading "Gestión de Eventos" y del botón "Crear Evento".
- **SC-004**: US2 confirma que aparece un mensaje visible asociado al campo Nombre indicando obligatoriedad y que el dialog permanece abierto con el campo Nombre y el botón de envío visibles.
- **SC-005**: Ejecutar cada escenario de forma aislada produce PASS sin depender del otro, después de preparar el estado por API.
- **SC-006**: El código no contiene comentarios ni nomenclatura no semántica.
- **SC-007**: Los escenarios son diferentes en página y flujo respecto a `AUTO_FRONT_POM_FACTORY`.
