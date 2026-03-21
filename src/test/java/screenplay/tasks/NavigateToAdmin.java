package screenplay.tasks;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Open;
import net.thucydides.model.util.EnvironmentVariables;

public class NavigateToAdmin implements Task {

    private final String adminUrl;

    public NavigateToAdmin() {
        EnvironmentVariables environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        String baseUrl = EnvironmentSpecificConfiguration.from(environmentVariables).getProperty("webdriver.base.url");
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("No se encontró la configuración webdriver.base.url en serenity.conf");
        }
        this.adminUrl = baseUrl.endsWith("/") ? baseUrl + "admin" : baseUrl + "/admin";
    }

    public static NavigateToAdmin page() {
        return Tasks.instrumented(NavigateToAdmin.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(adminUrl));
    }
}
