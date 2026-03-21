package steps;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.is;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.WebDriver;
import screenplay.questions.TheAdminPanel;
import screenplay.questions.TheCreateEventDialog;
import screenplay.questions.TheValidationError;
import screenplay.tasks.NavigateToAdmin;
import screenplay.tasks.OpenCreateEventForm;
import screenplay.tasks.PrepareAdminState;
import screenplay.tasks.SubmitEventFormWithoutName;

public class AdminPanelSteps {

    @Managed(driver = "chrome")
    private WebDriver navegador;

    @Before
    public void configurarEscenario() {
        OnStage.setTheStage(new OnlineCast());
        Actor administrador = OnStage.theActorCalled("Administrador");
        administrador.can(BrowseTheWeb.with(navegador));
    }

    @Dado("que el administrador navega al panel de administración")
    public void queElAdministradorNavegaAlPanelDeAdministracion() {
        prepararEstadoConocido();
        navegarAlPanelDeAdministracion();
    }

    @Dado("que el administrador se encuentra en el panel de administración")
    public void queElAdministradorSeEncuentraEnElPanelDeAdministracion() {
        prepararEstadoConocido();
        navegarAlPanelDeAdministracion();
    }

    @Entonces("el sistema muestra el heading Gestión de Eventos")
    public void elSistemaMuestraElHeadingGestionDeEventos() {
        validarPanelDeAdministracion();
    }

    @Y("el botón para crear un evento está disponible en la interfaz")
    public void elBotonParaCrearUnEventoEstaDisponibleEnLaInterfaz() {
        validarPanelDeAdministracion();
    }

    @Y("el administrador abre el formulario de creación de evento")
    public void elAdministradorAbreElFormularioDeCreacionDeEvento() {
        abrirFormularioDeCreacion();
    }

    @Cuando("intenta crear un evento sin ingresar el nombre")
    public void intentaCrearUnEventoSinIngresarElNombre() {
        enviarFormularioSinNombre();
    }

    @Entonces("el sistema muestra un mensaje de validación")
    public void elSistemaMuestraUnMensajeDeValidacion() {
        validarMensajeDeValidacion();
    }

    @Y("el administrador permanece en el formulario de creación")
    public void elAdministradorPermaneceEnElFormularioDeCreacion() {
        validarFormularioAbierto();
    }

    @Step("Preparar el estado conocido del módulo admin por API")
    public void prepararEstadoConocido() {
        actorActual().attemptsTo(PrepareAdminState.withKnownCatalog());
    }

    @Step("Navegar al panel de administración")
    public void navegarAlPanelDeAdministracion() {
        actorActual().attemptsTo(NavigateToAdmin.page());
    }

    @Step("Validar que el panel de administración está disponible")
    public void validarPanelDeAdministracion() {
        actorActual().should(seeThat(TheAdminPanel.isFullyLoaded(), is(true)));
    }

    @Step("Abrir el formulario de creación de eventos")
    public void abrirFormularioDeCreacion() {
        actorActual().attemptsTo(OpenCreateEventForm.now());
    }

    @Step("Enviar el formulario sin nombre")
    public void enviarFormularioSinNombre() {
        actorActual().attemptsTo(SubmitEventFormWithoutName.andSubmit());
    }

    @Step("Validar el mensaje de validación del formulario")
    public void validarMensajeDeValidacion() {
        actorActual().should(seeThat(TheValidationError.isVisible(), is(true)));
    }

    @Step("Validar que el formulario de creación permanece abierto")
    public void validarFormularioAbierto() {
        actorActual().should(seeThat(TheCreateEventDialog.remainsOpen(), is(true)));
    }

    private Actor actorActual() {
        return OnStage.theActorInTheSpotlight();
    }
}
