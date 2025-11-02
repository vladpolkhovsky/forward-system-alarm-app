package by.forwardsystem.dto;

import lombok.Data;

@Data
public class TelegramSendMessageBody {
    private final Long chat_id;
    private final String text;
}
