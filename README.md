# AUTO_FRONT_SCREENPLAY

Proyecto de automatización E2E para el flujo de administración de eventos de Ticketing usando Java, Serenity BDD, Selenium, Cucumber y patrón Screenplay.

## Objetivo

Este proyecto valida el comportamiento observable del panel `/admin` de la aplicación Ticketing.

Actualmente cubre estos escenarios:

- Visualización del panel de administración de eventos.
- Validación negativa al intentar crear un evento sin nombre.

## Alcance funcional

La automatización verifica:

- El heading `Gestión de Eventos`.
- La disponibilidad del botón para crear eventos.
- La apertura del diálogo de creación.
- La validación visible cuando el nombre está vacío.
- La permanencia del diálogo abierto después de un envío inválido.

## Stack técnico

- Java 21
- Gradle 9
- Serenity BDD 4.2.8
- Selenium WebDriver
- Cucumber
- JUnit 4
- Screenplay Pattern

## Estructura del proyecto

- `src/test/java/runners/`: runner de Cucumber con Serenity.
- `src/test/java/steps/`: definiciones de steps.
- `src/test/java/screenplay/ui/`: mapa de elementos UI.
- `src/test/java/screenplay/tasks/`: tareas Screenplay.
- `src/test/java/screenplay/questions/`: preguntas Screenplay.
- `src/test/resources/features/`: escenarios Gherkin.
- `src/test/resources/serenity.conf`: configuración de URLs y navegador.
- `target/site/serenity/`: reporte generado por Serenity.

## Repositorios relacionados

- Frontend Ticketing: https://github.com/emolinaf99/FrontendTicketing
- Backend Ticketing / microservicios: https://github.com/Jomruizgo/ticketing_project_week1

## Prerrequisitos

Antes de ejecutar las pruebas, se recomienda tener instalado:

- Java 21
- Google Chrome
- Docker y Docker Compose
- Node.js 18+ y npm
- Git

## Servicios que deben estar corriendo

Para que las pruebas funcionen, la aplicación bajo prueba debe estar disponible en los siguientes puertos:

- Frontend: `http://localhost:3000`
- CRUD Service: `http://localhost:8002`
- Producer Service: `http://localhost:8001` (dependencia del ecosistema Ticketing)
- Infraestructura de soporte del backend según el proyecto Ticketing: PostgreSQL, RabbitMQ y workers necesarios

## Configuración usada por este proyecto

Las URLs consumidas por la automatización están definidas en `src/test/resources/serenity.conf`:

- `webdriver.base.url = "http://localhost:3000"`
- `api.crud.base.url = "http://localhost:8002"`

Si el entorno usa otros puertos o hosts, ajustar ese archivo antes de ejecutar.

## Cómo levantar el entorno de Ticketing

### Opción 1: levantar frontend y backend manualmente

Usar esta opción si los repositorios del frontend y backend están separados.

#### 1. Levantar backend

El backend debe dejar operativo al menos:

- CRUD Service en puerto `8002`
- Producer Service en puerto `8001`
- Base de datos
- Broker de mensajería
- Workers necesarios del flujo Ticketing

Ejemplo de referencia:

1. Clonar el repositorio backend.
2. Levantar infraestructura con Docker Compose.
3. Confirmar que `http://localhost:8002/api/events` responda correctamente.
4. Confirmar que `http://localhost:8001/health` o el endpoint equivalente responda correctamente.

#### 2. Levantar frontend

1. Clonar el repositorio frontend.
2. Instalar dependencias.
3. Ejecutar el servidor de desarrollo o build local.
4. Confirmar que `http://localhost:3000/admin` cargue correctamente.

Ejemplo genérico:

```bash
npm install
npm run dev
```

### Opción 2: levantar todo el ecosistema desde un único proyecto Ticketing

Si el proyecto Ticketing ya incluye `docker compose` para los servicios backend:

```bash
docker compose up -d --build
```

Y luego, para el frontend:

```bash
npm install
npm run dev
```

## Validaciones previas recomendadas

Antes de lanzar las pruebas, validar manualmente:

```bash
curl http://localhost:8002/api/events
curl http://localhost:3000/admin
```

Resultados esperados:

- El CRUD Service debe responder sin error 500.
- La página `/admin` debe estar accesible.

## Cómo ejecutar las pruebas

Desde la raíz de este proyecto:

### Compilar clases de prueba

```bash
./gradlew testClasses
```

### Ejecutar solo el escenario de visualización del panel

```bash
./gradlew test --tests '*CucumberTestRunner*' -Dcucumber.filter.tags='@happy-path' aggregate
```

### Ejecutar solo el escenario de validación negativa

```bash
./gradlew test --tests '*CucumberTestRunner*' -Dcucumber.filter.tags='@error-path' aggregate
```

### Ejecutar toda la suite

```bash
./gradlew clean test aggregate
```

## Reporte de ejecución

Después de ejecutar la suite, el reporte principal queda en:

- `target/site/serenity/index.html`

Si se desea servir el reporte localmente:

```bash
cd target/site/serenity
python3 -m http.server 8765
```

Y luego abrir:

- `http://127.0.0.1:8765/index.html`

## Comportamiento de la automatización

Cada escenario prepara primero el estado del módulo admin por API antes de navegar al frontend. Esto permite mantener independencia entre escenarios.

Resumen del flujo:

1. Limpieza/normalización del estado por API.
2. Navegación a `/admin`.
3. Ejecución de interacciones UI.
4. Validación de resultados observables.
5. Generación del reporte Serenity.

## Troubleshooting

### El frontend no responde en `localhost:3000`

Verificar que el frontend del proyecto Ticketing esté levantado.

### El CRUD Service responde 500

Verificar:

- conexión a base de datos
- migraciones o esquema aplicado
- contenedores de soporte levantados
- logs del servicio CRUD

### Chrome abre pero falla Selenium

Verificar:

- que Google Chrome esté instalado
- que el entorno gráfico permita abrir navegador
- que no exista conflicto de puertos o políticas locales

### El reporte no abre directamente

Generar el reporte con:

```bash
./gradlew clean test aggregate
```

Luego abrir `target/site/serenity/index.html` o servirlo con `python3 -m http.server`.

## Próximos pasos sugeridos

- Reemplazar los placeholders de repositorios.
- Agregar pasos exactos del backend Ticketing según el repositorio real.
- Documentar credenciales, variables de entorno o seeds si el proyecto backend las requiere.
- Incluir capturas del reporte Serenity si se necesita para entrega.
