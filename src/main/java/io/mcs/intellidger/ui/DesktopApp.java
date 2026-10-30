package io.mcs.intellidger.ui;

import io.mcs.intellidger.IntellidgerApplication;
import io.mcs.intellidger.shared.persistence.AppDataFolder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * The JavaFX application. Spring starts before the window opens (the database is migrated by then) and closes with
 * it. Screens are FXML whose controllers are Spring beans, so a controller is handed its services like any other bean.
 */
public class DesktopApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        Path dataFolder = AppDataFolder.create();
        context = new SpringApplicationBuilder(IntellidgerApplication.class)
                .headless(false)
                .properties(Map.of(
                        "intellidger.data-folder", dataFolder.toString(),
                        "logging.file.name", dataFolder.resolve("logs").resolve("intellidger.log").toString()))
                .run(getParameters().getRaw().toArray(String[]::new));
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("main-window.fxml"));
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load();
        stage.setTitle(AppDataFolder.NAME);
        stage.setScene(new Scene(root, 1200, 760));
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}
