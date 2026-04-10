package com.ticketify.hooks;

import com.ticketify.util.Credenciales;
import com.ticketify.util.CredencialesBuilder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HooksAutenticacion {

    @Before
    public void inicializarEscenario() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Before("@CP-HU2-09")
    public void crearCuentaFrescaUUID() throws Exception {
        Credenciales creds = CredencialesBuilder.conEmailUUID();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8003/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(creds.toJson()))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        CredencialesBuilder.guardar(creds);
    }

    @After
    public void finalizarEscenario() {
        OnStage.drawTheCurtain();
        CredencialesBuilder.limpiar();
    }
}

