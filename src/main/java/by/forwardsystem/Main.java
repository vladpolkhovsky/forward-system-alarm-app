package by.forwardsystem;

import by.forwardsystem.dto.DatabaseConnectionInformation;
import by.forwardsystem.service.TelegramService;
import by.forwardsystem.utils.ConnectionUtils;
import by.forwardsystem.utils.ConstantsHolder;
import by.forwardsystem.utils.JsonUtils;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.nio.charset.Charset;
import java.util.Base64;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            String secretDBConnectionString = getSecretDBConnectionString();

            if (StringUtils.isBlank(secretDBConnectionString)) {
                System.exit(0);
            }

            String json = new String(Base64.getDecoder().decode(secretDBConnectionString), Charset.defaultCharset());
            DatabaseConnectionInformation connectionInformation = JsonUtils.parseJson(json, DatabaseConnectionInformation.class);

            ConnectionUtils.validateConnection(connectionInformation);
            TelegramService.validateToken(connectionInformation.getBotToken());

            ConstantsHolder.setConnectionInformation(connectionInformation);
            ConstantsHolder.setBotToken(connectionInformation.getBotToken());

            Application.launch(MainView.class, args);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(ex), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
    }

    private static String getSecretDBConnectionString() {
        String base64connectionInformation = null;

        while (true) {
            base64connectionInformation = JOptionPane.showInputDialog(null,
                    "Введите секрутную строку",
                    "Ввод секретной строки",
                    JOptionPane.QUESTION_MESSAGE);

            base64connectionInformation = StringUtils.trimToNull(base64connectionInformation);

            if (StringUtils.isNoneBlank(base64connectionInformation)) {
                return base64connectionInformation;
            }

            int code = JOptionPane.showConfirmDialog(null,
                    "Вы ввели неверную строку. Продолжить?",
                    "Неверные данные",
                    JOptionPane.YES_NO_OPTION);

            if (code == JOptionPane.NO_OPTION || code == JOptionPane.CLOSED_OPTION) {
                return null;
            }
        }
    }
}