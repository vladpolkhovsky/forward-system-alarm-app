package by.forwardsystem.utils;

import by.forwardsystem.Main;
import javafx.fxml.FXMLLoader;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@UtilityClass
public class Loader {

    public <T> T load(String view) {
        try {
            log.info("Loading view {}", view);
            return FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(Objects.requireNonNull(view))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
