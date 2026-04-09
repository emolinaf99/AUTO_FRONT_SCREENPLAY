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
