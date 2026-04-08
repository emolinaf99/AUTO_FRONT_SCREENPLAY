Feature: Consulta y reserva de tickets por parte del comprador
  Como comprador interesado en asistir a un evento
  Quiero consultar los eventos disponibles y reservar un ticket
  Para asegurar mi lugar antes de que se agoten las entradas

  Scenario Outline: El comprador visualiza los eventos publicados en la pagina principal
    Given el comprador accede a la pagina principal del sistema de ticketing
    When el sistema carga el listado de eventos disponibles
    Then el comprador puede ver al menos un evento con su nombre y fecha de realizacion
    And cada evento muestra la cantidad de tickets en estado "<estado_tickets>"

    Examples:
      | estado_tickets |
      | disponibles    |

  Scenario Outline: El comprador no puede reservar un ticket sin proporcionar "<campo_requerido>"
    Given el comprador se encuentra en la pagina de detalle de un evento con tickets disponibles
    When selecciona un ticket disponible para reservar
    And intenta confirmar la reserva sin ingresar "<campo_requerido>"
    Then el sistema no procesa la reserva
    But muestra un mensaje indicando que "<campo_requerido>" es obligatorio para continuar

    Examples:
      | campo_requerido    |
      | correo electronico |
