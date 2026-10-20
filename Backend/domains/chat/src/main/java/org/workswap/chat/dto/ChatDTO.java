package org.workswap.chat.dto;

import java.time.LocalDateTime;

import org.workswap.chat.enums.ChatStatus;
import org.workswap.chat.enums.ChatType;

public record ChatDTO(
    Long id,
    long unreadCount,
    String lastMessageText,
    LocalDateTime lastMessageTime,
    ChatStatus status,
    ChatType type,
    Long targetId
) {}
