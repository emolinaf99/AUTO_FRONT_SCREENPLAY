package com.ticketify.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class LoginUi {

    public static final Target CAMPO_EMAIL =
            Target.the("campo de email").located(By.id("login-email"));

    public static final Target CAMPO_PASSWORD =
            Target.the("campo de contraseña").located(By.id("login-password"));

    public static final Target BOTON_INGRESAR =
            Target.the("botón de ingresar").located(By.cssSelector("button[type='submit']"));

    public static final Target MENSAJE_BLOQUEO =
            Target.the("mensaje de cuenta bloqueada").located(By.cssSelector("p.mb-4.text-sm.text-red-400.text-center"));
}
