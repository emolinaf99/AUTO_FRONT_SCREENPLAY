package com.ticketify.questions;

import com.ticketify.ui.LoginUi;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class MensajeDeBloqueoCuenta implements Question<Boolean> {

    public static MensajeDeBloqueoCuenta esVisible() {
        return new MensajeDeBloqueoCuenta();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return LoginUi.MENSAJE_BLOQUEO.resolveFor(actor).isVisible();
    }
}
