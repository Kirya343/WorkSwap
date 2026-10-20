package org.workswap.chat.dto;

public record ChatMemberDTO(
    Long chatId,
    String sub,
    String name,
    String avatarUrl
) {}