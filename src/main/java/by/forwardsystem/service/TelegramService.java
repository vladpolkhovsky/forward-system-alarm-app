package by.forwardsystem.service;

import by.forwardsystem.dto.TelegramSendMessageBody;
import by.forwardsystem.dto.UserDto;
import by.forwardsystem.utils.ConstantsHolder;
import by.forwardsystem.utils.JsonUtils;
import lombok.Data;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class TelegramService {

    private static HttpClient httpClient = HttpClient.newHttpClient();

    public void validateToken(String token) {
        try {
            String baseUrl = getBaseUrl(token);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/getMe"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Telegram bot validation result: {}", response.body());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return;
            }

            throw new RuntimeException("Telegram api bad response" + response.body());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(ex), ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
    }

    public SendMessageResult sendMessage(List<UserDto> users, String message) {
        Map<UserDto, HttpResponse<String>> results = users.stream().parallel()
                .map(t -> {
                    HttpResponse<String> response = sendMessage(t, message);
                    return Map.entry(t, response);
                })
                .filter(t -> t.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<UserDto, Boolean> isSuccessful = results.entrySet().stream()
                .map(t -> Map.entry(t.getKey(), t.getValue().statusCode() >= 200 && t.getValue().statusCode() < 300))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new SendMessageResult(results, isSuccessful);
    }

    public HttpResponse<String> sendMessage(UserDto user, String message) {
        String jsonBody = JsonUtils.create(new TelegramSendMessageBody(user.getTelegramId(), message));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/sendMessage"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    private String getBaseUrl() {
        return getBaseUrl(ConstantsHolder.getBotToken());
    }

    private String getBaseUrl(String botToken) {
        return "https://api.telegram.org/bot" + botToken;
    }

    @Data
    public static class SendMessageResult {
        private final Map<UserDto, HttpResponse<String>> responses;
        private final Map<UserDto, Boolean> isSuccess;
    }
}
