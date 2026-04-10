package com.ticketify.tasks;

import com.ticketify.ui.LoginUi;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class IniciarSesion implements Task {

    private final String email;
    private final String password;

    public IniciarSesion(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static IniciarSesion como(String email, String password) {
        return instrumented(IniciarSesion.class, email, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url("http://localhost:5173/login"),
                Enter.theValue(email).into(LoginUi.CAMPO_EMAIL),
                Enter.theValue(password).into(LoginUi.CAMPO_PASSWORD),
                Click.on(LoginUi.BOTON_INGRESAR)
        );
    }
}
