package screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import screenplay.ui.AdminPage;

public class OpenCreateEventForm implements Task {

    public static OpenCreateEventForm now() {
        return Tasks.instrumented(OpenCreateEventForm.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Click.on(AdminPage.CREATE_EVENT_BUTTON),
            WaitUntil.the(AdminPage.CREATE_EVENT_DIALOG, WebElementStateMatchers.isVisible())
                .forNoMoreThan(10)
                .seconds()
        );
    }
}
