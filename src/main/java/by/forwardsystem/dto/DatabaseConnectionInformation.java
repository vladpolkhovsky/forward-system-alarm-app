package by.forwardsystem.dto;

import lombok.Data;

@Data
public class DatabaseConnectionInformation {
    private String jdbcUrl;
    private String username;
    private String password;
    private String botToken;
}
