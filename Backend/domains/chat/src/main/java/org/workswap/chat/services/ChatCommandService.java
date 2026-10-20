package org.workswap.chat.services;

import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.SendMessageDTO;
import org.workswap.sso.security.dto.UserAuthData;
import org.springframework.security.access.AccessDeniedException;
import org.workswap.chat.datasource.model.Chat;

public interface ChatCommandService {

    void sendMessage(SendMessageDTO message, UserAuthData authData) throws AccessDeniedException;
    void notifyChatUpdate(ChatDTO chatDto, String recipientSub);
    void markMessagesAsRead(Long chatId, UserAuthData authData);
    void setPermanentChat(Chat chat);
    void acceptChatTerms(Long chatId, UserAuthData authData);
    void deleteTemporaryChats(UserAuthData authData);
    void deleteUserFromChats(Long userId);
}
