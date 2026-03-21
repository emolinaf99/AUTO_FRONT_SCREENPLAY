#language: es
Característica: Gestión de eventos en panel de administración
  Como administrador del sistema
  Quiero gestionar los eventos desde el panel de administración
  Para validar la disponibilidad del panel y el comportamiento del formulario de creación

  @happy-path
  Escenario: El administrador visualiza el panel de gestión de eventos
    Dado que el administrador navega al panel de administración
    Entonces el sistema muestra el heading Gestión de Eventos
    Y el botón para crear un evento está disponible en la interfaz

  @error-path
  Escenario: El sistema bloquea la creación de evento cuando falta el nombre
    Dado que el administrador se encuentra en el panel de administración
    Y el administrador abre el formulario de creación de evento
    Cuando intenta crear un evento sin ingresar el nombre
    Entonces el sistema muestra un mensaje de validación
    Y el administrador permanece en el formulario de creación
