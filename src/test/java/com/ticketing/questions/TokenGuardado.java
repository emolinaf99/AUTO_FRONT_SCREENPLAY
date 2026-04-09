package com.ticketing.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.JavascriptExecutor;

public class TokenGuardado implements Question<String> {

    private static final String STORAGE_KEY = "auth_token";

    public static TokenGuardado enLocalStorage() {
        return new TokenGuardado();
    }

    @Override
    public String answeredBy(Actor actor) {
        JavascriptExecutor js = (JavascriptExecutor) BrowseTheWeb.as(actor).getDriver();
        return (String) js.executeScript("return window.localStorage.getItem('" + STORAGE_KEY + "');");
    }
}
