package org.workswap.chat.dto;

import java.time.LocalDateTime;

public record MessageDTO(
    Long id,
    String text,
    LocalDateTime sentAt,
    String senderSub,
    Long chatId,
    boolean isRead
) {}