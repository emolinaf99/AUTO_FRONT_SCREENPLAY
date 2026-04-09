# Requirements — AUTO_FRONT_SCREENPLAY
## Feature: Autenticación de Usuarios — Login, Logout y Bloqueo de Cuenta (HU2)

**Patrón**: Screenplay (Actor / Task / Question)
**App bajo prueba**: FrontendTicketing en `http://localhost:5173`
**Contexto**: Ver TEST_CASES.md en authService/ para la especificación completa.

---

## Restricción de Scope

Este repositorio cubre **exclusivamente** los escenarios de **login, logout y bloqueo de cuenta (HU2)**.
**Los escenarios de este repo NO pueden coincidir con los de AUTO_FRONT_POM_FACTORY.**
- Registro de usuario (HU1) → AUTO_FRONT_POM_FACTORY
- Pruebas de API REST → AUTO_API_SCREENPLAY

---

## HU2: Inicio y Cierre de Sesión

**Como** usuario registrado en el aplicativo,
**Quiero** iniciar sesión y poder cerrarla cuando lo desee,
**Para** acceder al historial de mis tickets y proteger mi cuenta.

### Rutas bajo prueba
| Ruta | Descripción |
|------|-------------|
| `http://localhost:5173/login` | Formulario de inicio de sesión |
| `http://localhost:5173/` | Vista principal (destino post-login) |
| `http://localhost:5173/admin` | Ruta protegida (requiere token válido) |

### Campos del formulario de login
| Campo | ID/selector esperado | Tipo |
|-------|---------------------|------|
| Correo Electrónico | `#login-email` | email |
| Contraseña | `#login-password` | password |
| Botón iniciar sesión | `button[type="submit"]` | button |
| Botón cerrar sesión | NavBar → botón "Cerrar sesión" | button |

### Datos de prueba válidos
```
email:    "carlos.gomez@sofka.com"    (usuario previamente registrado)
password: "SofkaTech2026!"
```

---

## Casos de Prueba — Obligatorios

### CP-HU2-01: Login exitoso con credenciales válidas
**Tipo**: Flujo Básico (Positivo) | **Prioridad**: Obligatorio

```gherkin
Dado que un usuario con rol de comprador está registrado en el sistema
  Y se encuentra en la página de inicio de sesión
Cuando ingresa su correo electrónico válido y su contraseña correcta
  Y hace clic en el botón de iniciar sesión
Entonces el sistema autentica al usuario exitosamente
  Y el sistema genera y almacena un token JWT válido en el cliente
  Y el usuario es redirigido a la vista principal de su cuenta
  Y el usuario puede visualizar su historial de tickets
```

**Resultado esperado**: token en `localStorage['auth_token']`, redirección a `/`, botón "Cerrar sesión" visible en NavBar
**Precondición**: usuario `carlos.gomez@sofka.com` registrado en el sistema

---

### CP-HU2-09: Bloqueo al tercer intento fallido consecutivo
**Tipo**: Regla de Negocio (Negativo) | **Prioridad**: Obligatorio | **Técnica**: Análisis de Valores Límite — RN5

```gherkin
Dado que un usuario registrado tiene un historial de 2 intentos fallidos consecutivos de inicio de sesión
Cuando ingresa credenciales inválidas por tercera vez consecutiva
  Y hace clic en el botón de iniciar sesión
Entonces el sistema deniega el acceso
  Y el sistema bloquea temporalmente la cuenta del usuario
  Y el sistema muestra un mensaje indicando que la cuenta ha sido bloqueada temporalmente por seguridad
```

**Resultado esperado**: mensaje de bloqueo visible, HTTP 423 del backend
**Precondición**: acumular 2 intentos fallidos previos como parte del escenario (pasos Given o llamadas de setup)

---

## Casos de Prueba — Recomendados (extras)

### CP-HU2-15: Cierre de sesión exitoso
**Tipo**: Flujo Básico (Positivo)

```gherkin
Dado que un usuario tiene una sesión activa en el sistema
  Y se encuentra en cualquier vista protegida
Cuando el usuario hace clic en la opción de cerrar sesión
Entonces el sistema invalida y elimina el token JWT del lado del cliente
  Y el sistema redirige al usuario a la vista pública principal
```

**Resultado esperado**: `localStorage['auth_token']` eliminado, redirección a `/login`, botón logout desaparece del NavBar

---

### CP-HU2-16: Acceso a ruta protegida tras cerrar sesión
**Tipo**: Flujo Alterno (Negativo)

```gherkin
Dado que un usuario ha cerrado su sesión exitosamente
Cuando intenta acceder directamente a la URL de su historial de tickets
Entonces el sistema verifica la ausencia del token
  Y el sistema bloquea el acceso
  Y el sistema redirige al usuario a la página de inicio de sesión
```

**Resultado esperado**: redirección a `/login` al intentar navegar a `/admin`

---

### CP-HU2-10: Login con cuenta ya bloqueada y contraseña correcta
**Tipo**: Excepción (Negativo)

```gherkin
Dado que la cuenta de un usuario registrado se encuentra bloqueada temporalmente por superar el límite de intentos
Cuando el usuario ingresa su correo electrónico y su contraseña correcta
  Y hace clic en el botón de iniciar sesión
Entonces el sistema deniega el acceso
  Y el sistema muestra un mensaje indicando que la cuenta está bloqueada y debe esperar el tiempo establecido
```

**Resultado esperado**: mensaje de bloqueo aunque la contraseña sea correcta

---

## Reglas de Negocio Validadas en UI

| ID | Regla | Validación |
|----|-------|------------|
| RN4 | Acceso solo a usuarios registrados | Backend retorna 401 |
| RN5 | Bloqueo temporal tras 3 intentos fallidos | Backend retorna 423 |
| — | Token JWT eliminado al cerrar sesión | `localStorage` limpio |
| — | Rutas protegidas redirigen sin token | Navigation guard Vue Router |

---

## Tasks Screenplay a Implementar

| Task | Responsabilidad única |
|------|-----------------------|
| `IniciarSesion` | Navegar a `/login`, completar campos, hacer submit |
| `CerrarSesion` | Hacer clic en botón "Cerrar sesión" del NavBar |
| `IntentarLoginConCredencialesInvalidas` | Completar login con password incorrecta y hacer submit |
| `NavegarsA` | Navegar a una URL específica |

## Questions Screenplay a Implementar

| Question | Retorna |
|----------|---------|
| `EstaAutenticado` | `Boolean` — verifica presencia de botón logout en NavBar |
| `MensajeDeError` | `String` — texto del mensaje de error visible en pantalla |
| `UrlActual` | `String` — URL actual del browser |

---

## Notas de Implementación

- El token se almacena en `localStorage` bajo la clave `auth_token`.
- El botón "Cerrar sesión" solo es visible en NavBar cuando `isAuthenticated = true`.
- La detección de bloqueo es por HTTP 423 del backend — el mensaje en UI es: "Tu cuenta ha sido bloqueada temporalmente por exceso de intentos fallidos".
- Para CP-HU2-09: los 2 intentos previos se ejecutan como pasos `When` dentro del mismo escenario para mantener independencia.
- El Actor debe inicializarse con `BrowseTheWeb.with(driver)` en `@Before` y el driver debe cerrarse en `@After`.
