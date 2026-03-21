# Contexto de la Aplicación Bajo Prueba

## Nombre del Sistema
TicketRush — Sistema de compra y reserva de tickets para eventos

## URL Base
`http://localhost:3000` (frontend Next.js)

## Servicios del Backend
| Servicio | Puerto | Responsabilidad |
|---|---|---|
| CRUD Service | `http://localhost:8002` | Gestión de eventos y tickets (GET/POST/PUT/DELETE) |
| Producer Service | `http://localhost:8001` | Reserva de tickets y procesamiento de pagos (asíncrono, RabbitMQ) |

## Páginas Disponibles

| Ruta | Descripción | Elementos clave visibles |
|---|---|---|
| `/buy` | Lista de eventos disponibles para comprar tickets | Heading "Compra de Tickets", tarjetas de eventos, mensaje "No hay eventos disponibles" si lista vacía |
| `/buy/[eventId]` | Detalle de un evento con lista de tickets reservables | Nombre del evento, botón "Reservar" por ticket |
| `/admin` | Panel de administración de eventos | Heading "Gestión de Eventos", botón "Crear Evento", tabla/lista de eventos existentes |

## NO existe en la aplicación
- Pantalla de login / autenticación
- Registro de usuarios
- Perfil de usuario
- Roles con contraseña

## Página /admin — Flujo de Gestión de Eventos (objetivo de este proyecto)

### Visualizar el panel
1. El usuario navega a `/admin`
2. Ve el heading **"Gestión de Eventos"**
3. Ve la lista de eventos existentes (o tabla vacía si no hay eventos)
4. Ve el botón **"Crear Evento"**

### Crear un evento (flujo positivo)
1. El usuario hace clic en "Crear Evento"
2. Se abre un dialog/modal con formulario: campo **Nombre** (requerido), otros campos opcionales
3. Completa el formulario con datos válidos y envía
4. El evento aparece en la lista del panel de administración

### Crear un evento sin nombre (flujo negativo)
1. El usuario hace clic en "Crear Evento"
2. Se abre el dialog de creación
3. Deja el campo **Nombre** vacío y envía el formulario
4. Aparece validación de error (campo requerido o mensaje equivalente)
5. El usuario permanece en el dialog sin que se cierre

## Validaciones de UI verificables en /admin
| Acción | Resultado esperado observable |
|---|---|
| Navegar a `/admin` | Heading "Gestión de Eventos" visible |
| Hacer clic en "Crear Evento" | Dialog/modal de creación visible |
| Enviar formulario con nombre válido | Evento creado y listado en el panel |
| Enviar formulario sin nombre | Error de validación visible, dialog permanece abierto |

## Diferencia con AUTO_FRONT_POM_FACTORY
AUTO_FRONT_POM_FACTORY automatiza el flujo `/buy` (compra pública de tickets).
Este proyecto (AUTO_FRONT_SCREENPLAY) automatiza el flujo `/admin` (panel de gestión).
Los escenarios son completamente distintos en página, flujo y usuarios.

## Datos de Prueba Necesarios
- Cada escenario debe restablecer por API el estado observable del módulo admin antes de navegar a `/admin`
- La preparación mínima consiste en dejar el CRUD Service disponible y el catálogo de eventos en estado conocido
- Para la validación de creación: interacción directa con el formulario del dialog sobre un estado preparado por API
- No se requieren credenciales de usuario (sin autenticación)
