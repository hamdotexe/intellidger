package io.mcs.intellidger;

import io.mcs.intellidger.ui.DesktopApp;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts Intellidger. Not itself the JavaFX application: a main class extending {@code Application} refuses to start
 * from a plain classpath, so this one hands over to {@link DesktopApp}, which brings up Spring and then the window.
 */
@SpringBootApplication
public class IntellidgerApplication {

    public static void main(String[] args) {
        Application.launch(DesktopApp.class, args);
    }
}
