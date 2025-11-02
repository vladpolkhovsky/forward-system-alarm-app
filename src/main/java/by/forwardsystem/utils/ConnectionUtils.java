package by.forwardsystem.utils;

import by.forwardsystem.dto.DatabaseConnectionInformation;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
@UtilityClass
public class ConnectionUtils {

    private static Path CA_PEM_PATH = Path.of("./CA.pem").toAbsolutePath();


    public static void validateConnection(DatabaseConnectionInformation connectionInformation) {
        execute(connectionInformation, connection -> {
            try {
                ResultSet resultSet = connection.prepareStatement("select version()").executeQuery();
                resultSet.next();
                log.info("Validate connection result: {}", resultSet.getObject(1));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void execute(DatabaseConnectionInformation connectionInformation, Consumer<Connection> connectionConsumer) {
        try {
            fetchCa();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(ex), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }

        Properties properties = new Properties();
        properties.setProperty("user", connectionInformation.getUsername());
        properties.setProperty("password", connectionInformation.getPassword());
        properties.setProperty("connectTimeout", "10000");
        properties.setProperty("sslmode", "verify-full");
        properties.setProperty("sll", "true");
        properties.setProperty("sslrootcert", CA_PEM_PATH.toString());
        properties.setProperty("targetServerType", "master");


        try (Connection connection = DriverManager.getConnection(connectionInformation.getJdbcUrl(), properties)) {
            connectionConsumer.accept(connection);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(ex), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
    }


    @SneakyThrows
    private static void fetchCa() {
        try (InputStream inputStream = new URL("https://storage.yandexcloud.net/cloud-certs/CA.pem").openStream()) {
            Files.deleteIfExists(CA_PEM_PATH);
            Files.createFile(CA_PEM_PATH);
            Files.write(CA_PEM_PATH, inputStream.readAllBytes());
        }
    }
}
