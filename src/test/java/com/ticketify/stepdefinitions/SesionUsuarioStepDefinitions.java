package com.ticketify.stepdefinitions;

import com.ticketify.questions.MensajeDeBloqueoCuenta;
import com.ticketify.questions.TokenGuardado;
import com.ticketify.questions.UrlActual;
import com.ticketify.tasks.IniciarSesion;
import com.ticketify.ui.HomeUi;
import com.ticketify.util.CredencialesBuilder;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public class SesionUsuarioStepDefinitions {

    private static final String WRONG_PASSWORD = "ContraseñaIncorrecta!99";

    @Dado("que el usuario está en la página de login de FrontendTicketing")
    public void elUsuarioEstaEnLaPaginaDeLogin() {
        OnStage.theActorCalled("Usuario");
    }

    @Cuando("inicia sesión con credenciales válidas")
    public void iniciaSesionConCredencialesValidas() {
        String email = System.getProperty("test.credentials.email");
        String password = System.getProperty("test.credentials.password");
        OnStage.theActorInTheSpotlight().attemptsTo(
                IniciarSesion.como(email, password)
        );
    }

    @Entonces("es redirigido a la página de inicio")
    public void esRedirigidoAlInicio() {
        OnStage.theActorInTheSpotlight().attemptsTo(
                WaitUntil.the(HomeUi.TITULO_PAGINA, isVisible()).forNoMoreThan(10).seconds()
        );
        OnStage.theActorInTheSpotlight().should(
                seeThat("la URL de inicio", UrlActual.delNavegador(), not(containsString("login")))
        );
    }

    @Y("el token JWT queda guardado en el almacenamiento local")
    public void elTokenJwtQuedaGuardado() {
        OnStage.theActorInTheSpotlight().should(
                seeThat("el token guardado no es nulo", TokenGuardado.enLocalStorage(), notNullValue()),
                seeThat("el token guardado no está vacío", TokenGuardado.enLocalStorage(), not(emptyOrNullString()))
        );
    }

    @Cuando("intenta iniciar sesión con credenciales incorrectas por primera vez")
    public void intentarLoginInvalidoPrimeraVez() {
        String email = CredencialesBuilder.obtener().getEmail();
        OnStage.theActorInTheSpotlight().attemptsTo(
                IniciarSesion.como(email, WRONG_PASSWORD)
        );
    }

    @Y("intenta iniciar sesión con credenciales incorrectas por segunda vez")
    public void intentarLoginInvalidoSegundaVez() {
        String email = CredencialesBuilder.obtener().getEmail();
        OnStage.theActorInTheSpotlight().attemptsTo(
                IniciarSesion.como(email, WRONG_PASSWORD)
        );
    }

    @Y("intenta iniciar sesión con credenciales incorrectas por tercera vez")
    public void intentarLoginInvalidoTerceraVez() {
        String email = CredencialesBuilder.obtener().getEmail();
        OnStage.theActorInTheSpotlight().attemptsTo(
                IniciarSesion.como(email, WRONG_PASSWORD)
        );
    }

    @Entonces("la aplicación muestra el mensaje de cuenta bloqueada")
    public void laAplicacionMuestraElMensajeDeBloqueoCuenta() {
        OnStage.theActorInTheSpotlight().should(
                seeThat("el mensaje de cuenta bloqueada es visible",
                        MensajeDeBloqueoCuenta.esVisible(), is(true))
        );
    }
}
