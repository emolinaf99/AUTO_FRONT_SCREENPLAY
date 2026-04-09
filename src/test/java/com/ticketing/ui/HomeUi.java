package com.ticketing.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class HomeUi {

    public static final Target TITULO_PAGINA =
            Target.the("título de la página de inicio").located(By.cssSelector("h1"));
}
