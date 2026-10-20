package org.workswap.chat.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.workswap.chat.dto.ChatDTO;
import org.workswap.chat.dto.MessageDTO;
import org.workswap.chat.dto.SendMessageDTO;
import org.workswap.chat.services.ChatCommandService;
import org.workswap.chat.services.ChatQueryService;
import org.workswap.shared.dto.PageRequestDTO;
import org.workswap.sso.security.annotations.controllers.Authenticated;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.dto.ShortUserDTO;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

    private final ChatQueryService chatQueryService;
    private final ChatCommandService chatCommandService;

    @MessageMapping("/chat.message-send")
    @RequiredPermission("CHAT_SEND_MESSAGE")
    public void sendMessage(
        SendMessageDTO message, 
        @AuthUser UserAuthData authData
    ) throws AccessDeniedException {
        chatCommandService.sendMessage(message, authData);
    }

    @MessageMapping("/chat.loadMessages/{chatId}")
    @SendToUser("/queue/chat/history.messages/{chatId}")
    @RequiredPermission("CHAT_LOAD_HISTORY")
    public List<MessageDTO> loadMessagesForChat(
        @DestinationVariable Long chatId, 
        PageRequestDTO pageRequest,
        @AuthUser UserAuthData authData
    ) {
        return chatQueryService.getMessagesByChatId(pageRequest.page(), chatId, authData);
    }

    @MessageMapping("/chat.markAsRead/{chatId}")
    @RequiredPermission("CHAT_MARK_AS_READ")
    public void markAsRead(
        @DestinationVariable Long chatId, 
        @AuthUser UserAuthData authData
    ) {
        chatCommandService.markMessagesAsRead(chatId, authData);
    }

    @MessageMapping("/chat.get-chats")
    @SendToUser("/queue/chats")
    @RequiredPermission("CHAT_GET_CHATS")
    public List<ChatDTO> getChats(@AuthUser UserAuthData authData, String locale) {
        return chatQueryService.getChatsDTOForUser(authData, locale);
    }

    @Transactional
    @MessageMapping("/chat.get-interlocutor-info/{chatId}")
    @SendToUser("/queue/chat/interlocutor-info")
    @RequiredPermission("CHAT_GET_INTERLOCUTOR")
    public Map<Long, List<ShortUserDTO>> getChatInterlocutors(
        @DestinationVariable Long chatId, 
        @AuthUser UserAuthData authData
    ) {
        return Map.of(chatId, chatQueryService.getChatInterlocutors(chatId, authData));
    }

    @MessageMapping("/messages.get-unread")
    @SendToUser("/queue/chat/messages")
    @Authenticated
    public List<MessageDTO> getChatUnreadMessages(@AuthUser UserAuthData authData) {
        logger.debug("Ауфдата: {}", authData.toString());
        return chatQueryService.getChatUnreadMessages(authData);
    }
}