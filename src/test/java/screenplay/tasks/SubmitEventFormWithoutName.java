package screenplay.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.waits.WaitUntil;
import screenplay.ui.AdminPage;

public class SubmitEventFormWithoutName implements Task {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static SubmitEventFormWithoutName andSubmit() {
        return Tasks.instrumented(SubmitEventFormWithoutName.class);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            WaitUntil.the(AdminPage.CREATE_EVENT_DIALOG, WebElementStateMatchers.isVisible())
                .forNoMoreThan(10)
                .seconds(),
            Clear.field(AdminPage.EVENT_NAME_FIELD),
            Enter.theValue(nextStartDate()).into(AdminPage.EVENT_DATE_FIELD),
            Click.on(AdminPage.SUBMIT_BUTTON),
            WaitUntil.the(AdminPage.VALIDATION_ERROR, WebElementStateMatchers.isVisible())
                .forNoMoreThan(10)
                .seconds()
        );
    }

    private String nextStartDate() {
        return LocalDateTime.now()
            .plusDays(1)
            .withSecond(0)
            .withNano(0)
            .format(DATE_TIME_FORMAT);
    }
}
