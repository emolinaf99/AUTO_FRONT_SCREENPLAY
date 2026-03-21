package screenplay.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import screenplay.ui.AdminPage;

public class TheCreateEventDialog implements Question<Boolean> {

    public static TheCreateEventDialog remainsOpen() {
        return new TheCreateEventDialog();
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        return AdminPage.CREATE_EVENT_DIALOG.resolveFor(actor).isVisible()
            && AdminPage.EVENT_NAME_FIELD.resolveFor(actor).isVisible()
            && AdminPage.EVENT_DATE_FIELD.resolveFor(actor).isVisible()
            && AdminPage.SUBMIT_BUTTON.resolveFor(actor).isVisible();
    }
}
