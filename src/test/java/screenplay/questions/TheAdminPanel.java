package screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import screenplay.ui.AdminPage;

public class TheAdminPanel implements Question<Boolean> {

    public static TheAdminPanel isFullyLoaded() {
        return new TheAdminPanel();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return AdminPage.HEADING.resolveFor(actor).isVisible()
            && AdminPage.CREATE_EVENT_BUTTON.resolveFor(actor).isVisible();
    }
}
