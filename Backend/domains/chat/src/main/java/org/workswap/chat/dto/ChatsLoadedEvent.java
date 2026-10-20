package org.workswap.chat.dto;

import java.util.List;

import org.workswap.sso.security.dto.UserAuthData;

public record ChatsLoadedEvent(
    UserAuthData authData,
    List<ChatDTO> chats,
    String locale
) {}
