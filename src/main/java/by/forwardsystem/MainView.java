package by.forwardsystem;

import by.forwardsystem.utils.Loader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SplitPane tabPane = Loader.load("main-view.fxml");

        Scene scene = new Scene(tabPane);

        stage.setTitle("Отправка уведомления");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
