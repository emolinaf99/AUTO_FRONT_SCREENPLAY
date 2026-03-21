package screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import screenplay.ui.AdminPage;

public class TheValidationError implements Question<Boolean> {

    public static TheValidationError isVisible() {
        return new TheValidationError();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return AdminPage.VALIDATION_ERROR.resolveFor(actor).isVisible()
            && AdminPage.VALIDATION_ERROR.resolveFor(actor).getText().contains("Completa todos los campos");
    }
}
