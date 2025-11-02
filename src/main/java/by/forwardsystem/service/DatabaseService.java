package by.forwardsystem.service;

import by.forwardsystem.dto.UserDto;
import by.forwardsystem.utils.ConnectionUtils;
import by.forwardsystem.utils.ConstantsHolder;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DatabaseService {

    public List<UserDto> fetchUsers() {
        ArrayList<UserDto> users = new ArrayList<>();

        ConnectionUtils.execute(ConstantsHolder.getConnectionInformation(), connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("""
                            select u.id, u.username, bid.telegram_chat_id
                                 from forward_system.users u
                                          inner join forward_system.bot_integration_data bid on u.id = bid.user_id
                                 where u.roles not like '%BANNED%'
                                 order by u.id asc
                        """);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    users.add(new UserDto(resultSet.getLong(1), resultSet.getString(2), resultSet.getLong(3)));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e), e.getMessage(), JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException(e);
            }
        });

        users.sort(Comparator.comparing(UserDto::getUsername));

        return users;
    }
}
