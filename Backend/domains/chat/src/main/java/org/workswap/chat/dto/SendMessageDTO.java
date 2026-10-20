package org.workswap.chat.dto;

public record SendMessageDTO(
    String text,
    String senderSub,
    Long chatId
) {
}
