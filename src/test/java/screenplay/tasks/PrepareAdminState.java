package screenplay.tasks;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.serenitybdd.model.environment.ConfiguredEnvironment;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.thucydides.model.util.EnvironmentVariables;

public class PrepareAdminState implements Task {

    private static final List<String> EVENTS_ENDPOINT_CANDIDATES = List.of("/api/events", "/events");

    private final String crudBaseUrl;

    public PrepareAdminState() {
        EnvironmentVariables environmentVariables = ConfiguredEnvironment.getEnvironmentVariables();
        this.crudBaseUrl = EnvironmentSpecificConfiguration.from(environmentVariables)
            .getProperty("api.crud.base.url");
    }

    public static PrepareAdminState withKnownCatalog() {
        return Tasks.instrumented(PrepareAdminState.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (crudBaseUrl == null || crudBaseUrl.isBlank()) {
            throw new IllegalStateException("No se encontró la configuración api.crud.base.url en serenity.conf");
        }

        Response response = null;
        String resolvedEndpoint = null;
        List<String> failures = new ArrayList<>();

        for (String endpoint : EVENTS_ENDPOINT_CANDIDATES) {
            Response candidateResponse = SerenityRest.given()
                .baseUri(crudBaseUrl)
                .accept(ContentType.JSON)
                .get(endpoint);

            int candidateStatus = candidateResponse.getStatusCode();
            if (candidateStatus == 200 || candidateStatus == 204) {
                response = candidateResponse;
                resolvedEndpoint = endpoint;
                break;
            }

            failures.add(endpoint + " -> " + candidateStatus);
        }

        if (response == null || resolvedEndpoint == null) {
            throw new IllegalStateException(
                "No fue posible consultar el catálogo de eventos. Intentos: " + String.join(", ", failures)
            );
        }

        int statusCode = response.getStatusCode();

        List<Map<String, Object>> events = statusCode == 204
            ? Collections.emptyList()
            : response.jsonPath().getList("$");

        if (events == null || events.isEmpty()) {
            return;
        }

        for (Map<String, Object> event : events) {
            Object eventId = event.get("id");
            if (eventId == null) {
                continue;
            }

            Response deleteResponse = SerenityRest.given()
                .baseUri(crudBaseUrl)
                .delete(resolvedEndpoint + "/{id}", eventId);

            int deleteStatus = deleteResponse.getStatusCode();
            if (deleteStatus >= 400 && deleteStatus != 404) {
                throw new IllegalStateException(
                    "No fue posible limpiar el evento " + eventId + ". Estado recibido: " + deleteStatus
                );
            }
        }
    }
}
