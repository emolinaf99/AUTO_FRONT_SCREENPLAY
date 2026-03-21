package screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public final class AdminPage {

    private AdminPage() {
    }

    public static final Target HEADING = Target.the("heading de gestión de eventos")
        .locatedBy("//h1[contains(normalize-space(.),'Gestión de Eventos')]");

    public static final Target CREATE_EVENT_BUTTON = Target.the("botón principal para crear un evento")
        .locatedBy("//button[contains(normalize-space(.),'Nuevo Evento') or contains(normalize-space(.),'Crear Evento')]");

    public static final Target CREATE_EVENT_DIALOG = Target.the("diálogo de creación de evento")
        .locatedBy("//*[@role='dialog']");

    public static final Target EVENT_NAME_FIELD = Target.the("campo nombre del evento")
        .locatedBy("//*[@id='event-name' or @name='name']");

    public static final Target EVENT_DATE_FIELD = Target.the("campo fecha y hora del evento")
        .locatedBy("//*[@id='event-date' or @type='datetime-local']");

    public static final Target SUBMIT_BUTTON = Target.the("botón de envío del formulario de evento")
        .locatedBy("//*[@role='dialog']//button[@type='submit' and (contains(normalize-space(.),'Crear Evento') or contains(normalize-space(.),'Creando'))]");

    public static final Target VALIDATION_ERROR = Target.the("mensaje de validación del formulario")
        .locatedBy("//*[@data-sonner-toast and contains(normalize-space(.),'Completa todos los campos')] | //*[@role='alert' and contains(normalize-space(.),'Completa todos los campos')] | //*[@role='status' and contains(normalize-space(.),'Completa todos los campos')]");
}
