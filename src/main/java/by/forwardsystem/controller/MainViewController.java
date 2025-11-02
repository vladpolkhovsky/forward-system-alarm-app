package by.forwardsystem.controller;

import by.forwardsystem.dto.UserDto;
import by.forwardsystem.service.DatabaseService;
import by.forwardsystem.service.TelegramService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
public class MainViewController implements Initializable {

    private static Map<Long, UserDto> USERS = new HashMap<>();
    private static Map<Long, Boolean> IS_CHECKED_USER = new HashMap<>();

    @FXML
    private ProgressIndicator progress;

    @FXML
    private TableView<UserTableDto> notificationTableView;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textArea;

    @FXML
    private StringProperty countSelectedProperty = new SimpleStringProperty();

    @FXML
    private Label countSelectedLabel;

    @FXML
    private Label countAllLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progress.setVisible(false);

        DatabaseService databaseService = new DatabaseService();

        TableColumn<UserTableDto, Boolean> isCheckedColumn = new TableColumn<>("Отправка");
        isCheckedColumn.setCellValueFactory(new PropertyValueFactory<>("checked"));
        isCheckedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isCheckedColumn));
        isCheckedColumn.setEditable(true);

        TableColumn<UserTableDto, String> usernameColumn = new TableColumn<>("Имя пользователя");
        usernameColumn.setCellValueFactory(data -> data.getValue().getUsername());
        usernameColumn.setEditable(false);

        List<UserDto> userDtos = databaseService.fetchUsers();

        notificationTableView.getColumns().addAll(List.of(isCheckedColumn, usernameColumn));
        notificationTableView.setItems(FXCollections.observableList(userDtos.stream()
                .peek(user -> USERS.put(user.getId(), user))
                .peek(user -> IS_CHECKED_USER.put(user.getId(), true))
                .map(UserTableDto::new)
                .toList()));

        notificationTableView.getItems().forEach(item -> {
            item.checkedProperty().addListener((observable, oldValue, newValue) -> {
                log.info("Change state of {} from {} to {}", item.getUsername().get(), oldValue, newValue);
                IS_CHECKED_USER.put(item.getId().get(), newValue);
                refreshSelectedCount();
            });
        });

        notificationTableView.setEditable(true);

        countSelectedProperty.addListener((observable, oldValue, newValue) -> {
            countAllLabel.setText(newValue);
            countSelectedLabel.setText(newValue);
        });

        refreshSelectedCount();

        sendButton.setOnMouseClicked(event -> {
            if (StringUtils.isBlank(textArea.getText())) {
                JOptionPane.showMessageDialog(null, "Пустое текстовое поле!");
                return;
            }

            List<UserDto> toSendUsers = IS_CHECKED_USER.entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .map(USERS::get)
                    .toList();

            if (toSendUsers.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Вы не выбрали пользователей для отправки!");
                return;
            }

            sendButton.setDisable(true);
            progress.setVisible(true);

            new Thread(() -> {
                try {
                    TelegramService.SendMessageResult sendMessageResult = TelegramService.sendMessage(toSendUsers, textArea.getText());
                    long count = sendMessageResult.getIsSuccess().values().stream().filter(BooleanUtils::isTrue).count();
                    JOptionPane.showMessageDialog(null, "Сообщения отправлены. Успешно отправлено " + count);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e), e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }
                sendButton.setDisable(false);
                progress.setVisible(false);
            }).start();
        });
    }

    public void refreshSelectedCount() {
        countSelectedProperty.setValue(String.valueOf(IS_CHECKED_USER.values().stream()
                .filter(BooleanUtils::isTrue)
                .count()));
    }

    @Data
    public static class UserTableDto {

        private final SimpleLongProperty id;
        private final SimpleLongProperty telegramId;
        private final SimpleStringProperty username;
        private final SimpleBooleanProperty checked;

        public UserTableDto(UserDto userDto) {
            this.id = new SimpleLongProperty(userDto.getId());
            this.telegramId = new SimpleLongProperty(userDto.getTelegramId());
            this.username = new SimpleStringProperty(userDto.getUsername());
            this.checked = new SimpleBooleanProperty(true);
        }

        public BooleanProperty checkedProperty() {
            return checked;
        }
    }
}
